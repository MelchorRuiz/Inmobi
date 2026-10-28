from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.config import settings
from app.database import get_db
from app.dependencies import require_roles
from app.email import send_verification_email
from app.models.end_user import EndUser
from app.models.user import Role, User
from app.schemas.user import UserRegister, UserResponse, UserProfileUpdate
from app.security import create_verification_token, hash_password

router = APIRouter(prefix="/end-users", tags=["End Users"])


@router.post(
    "/register/",
    response_model=UserResponse,
    status_code=status.HTTP_201_CREATED,
)
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
        email_verified=False,
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
    db.flush()

    token = create_verification_token(db_user.id, db_user.email)
    verification_url = f"{settings.API_BASE_URL}/auth/verify-email/?token={token}"
    send_verification_email(db_user.email, verification_url)
    db_user.email_verification_sent_at = datetime.now(timezone.utc)

    db.commit()
    db.refresh(db_user)
    return db_user


@router.patch("/me/", response_model=UserResponse)
def update_my_profile(
    payload: UserProfileUpdate,
    current_user: User = Depends(require_roles(Role.USER)),
    db: Session = Depends(get_db),
):
    data = payload.model_dump(exclude_unset=True)

    for key in ("full_name", "birth_date"):
        if key in data:
            setattr(current_user, key, data[key])

    end_user = current_user.end_user
    if end_user is None:
        end_user = EndUser(user_id=current_user.id)
        db.add(end_user)

    end_user_keys = ("phone", "privacy_accepted", "preference")
    for key in end_user_keys:
        if key not in data:
            continue
        if key == "phone" and data[key] is not None:
            taken = db.query(EndUser).filter(EndUser.phone == data[key]).first()
            if taken is not None and taken.user_id != current_user.id:
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="phone number is already registered",
                )
        setattr(end_user, key, data[key])

    db.commit()
    db.refresh(current_user)
    return current_user
