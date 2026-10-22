import jwt
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.config import settings
from app.database import get_db
from app.dependencies import get_current_user
from app.google_auth import GoogleIdentity, verify_google_id_token
from app.models.end_user import EndUser
from app.models.user import Role, User
from app.schemas.user import (
    GoogleLogin,
    SessionResponse,
    UserLogin,
    UserResponse,
)
from app.security import create_access_token, verify_password

router = APIRouter(prefix="/auth", tags=["Auth"])


@router.post("/login/", response_model=SessionResponse)
def login(user_in: UserLogin, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == user_in.email).first()
    if user is None or not _verify_credentials(user_in.password, user):
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

    return _build_session(user)


@router.post("/google/", response_model=SessionResponse)
def login_with_google(payload: GoogleLogin, db: Session = Depends(get_db)):
    if not settings.GOOGLE_CLIENT_ID:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="google sign-in is not configured",
        )

    try:
        identity = verify_google_id_token(payload.id_token, audience=settings.GOOGLE_CLIENT_ID)
    except jwt.PyJWTError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="invalid google credentials",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc

    user = db.query(User).filter(User.email == identity.email).first()

    if user is not None and not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="this account is deactivated",
        )

    if user is None:
        user = _create_google_user(db, identity)
    else:
        _link_google_identity(user, identity)
        db.commit()
        db.refresh(user)

    return _build_session(user)


@router.get("/me/", response_model=UserResponse)
def read_me(current_user: User = Depends(get_current_user)):
    return current_user


def _verify_credentials(password: str, user: User | None) -> bool:
    if user is None or user.password_hash is None:
        return False
    return verify_password(password, user.password_hash)


def _build_session(user: User) -> SessionResponse:
    access_token = create_access_token(subject=str(user.id), role=user.role.value)
    return SessionResponse(
        access_token=access_token,
        role=user.role,
        is_profile_complete=user.is_profile_complete,
        has_profile_picture=user.has_profile_picture,
    )


def _create_google_user(db: Session, identity: GoogleIdentity) -> User:
    user = User(
        full_name=identity.full_name,
        email=identity.email,
        password_hash=None,
        avatar_url=identity.picture_url,
        google_id=identity.google_id,
        role=Role.USER,
        is_active=True,
    )
    db.add(user)
    db.flush()

    db.add(EndUser(user_id=user.id))
    db.commit()
    db.refresh(user)
    return user


def _link_google_identity(user: User, identity: GoogleIdentity) -> None:
    if user.google_id is not None and user.google_id != identity.google_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="this email is already linked to a different google account",
        )
    if user.google_id is None:
        user.google_id = identity.google_id
    if user.avatar_url is None and identity.picture_url is not None:
        user.avatar_url = identity.picture_url
