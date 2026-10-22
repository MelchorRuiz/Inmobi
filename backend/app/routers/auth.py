from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import get_current_user
from app.models.end_user import EndUser
from app.models.user import Role, User
from app.schemas.user import Token, UserLogin, UserRegister, UserResponse
from app.security import create_access_token, hash_password, verify_password

router = APIRouter(prefix="/auth", tags=["Auth"])


@router.post("/register/end_users/", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
def register_end_user(user_in: UserRegister, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.email == user_in.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="email is already registered",
        )

    existing_phone = db.query(EndUser).filter(EndUser.phone == user_in.phone).first()
    if existing_phone:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="phone number is already registered",
        )

    db_user = User(
        full_name=user_in.full_name,
        email=user_in.email,
        password_hash=hash_password(user_in.password),
        birth_date=user_in.birth_date,
        role=Role.USER,
        is_active=True,
    )
    db.add(db_user)
    db.flush()

    db.add(
        EndUser(
            user_id=db_user.id,
            phone=user_in.phone,
            privacy_accepted=user_in.privacy_accepted,
            preference=user_in.preference,
        )
    )
    db.commit()
    db.refresh(db_user)
    return db_user


@router.post("/login/", response_model=Token)
def login(user_in: UserLogin, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == user_in.email).first()
    if user is None or not verify_password(user_in.password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="this account is deactivated",
        )

    access_token = create_access_token(subject=str(user.id), role=user.role.value)
    return Token(access_token=access_token)


@router.get("/me/", response_model=UserResponse)
def read_me(current_user: User = Depends(get_current_user)):
    return current_user
