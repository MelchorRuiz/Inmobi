import jwt
from datetime import datetime, timedelta, timezone

from fastapi import APIRouter, Cookie, Depends, HTTPException, Response, status
from sqlalchemy.orm import Session

from app.config import settings
from app.database import get_db
from app.dependencies import get_current_user
from app.google_auth import GoogleIdentity, verify_google_id_token
from app.models.end_user import EndUser
from app.models.refresh_token import ClientType
from app.models.user import Role, User
from app.schemas.user import (
    GoogleLogin,
    LogoutRequest,
    RefreshRequest,
    ResendVerificationRequest,
    SessionResponse,
    UserLogin,
    UserResponse,
    VerificationResponse,
)
from app.security import (
    create_access_token,
    create_verification_token,
    decode_verification_token,
    verify_password,
)
from app.sessions import (
    IssuedRefreshToken,
    RefreshTokenError,
    RefreshTokenReuseError,
    clear_refresh_cookie,
    issue_refresh_token,
    revoke_refresh_token,
    rotate_refresh_token,
    set_refresh_cookie,
)

router = APIRouter(prefix="/auth", tags=["Auth"])

RESEND_COOLDOWN_SECONDS = 60


@router.post("/login/", response_model=SessionResponse)
def login(
    user_in: UserLogin,
    response: Response,
    db: Session = Depends(get_db),
):
    user = db.query(User).filter(User.email == user_in.email).first()
    if user is None or not _verify_credentials(user_in.password, user):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )

    if not user.email_verified:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="please verify your email first",
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="this account is deactivated",
        )

    return _build_session(user, db, user_in.client_type, response)


@router.post("/google/", response_model=SessionResponse)
def login_with_google(
    payload: GoogleLogin,
    response: Response,
    db: Session = Depends(get_db),
):
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

    if user is not None:
        if not user.is_active:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="this account is deactivated",
            )
        if not user.email_verified:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="please verify your email first",
            )

    if user is None:
        user = _create_google_user(db, identity)
    else:
        _link_google_identity(user, identity)
        db.commit()
        db.refresh(user)

    return _build_session(user, db, payload.client_type, response)


@router.get("/verify-email/", response_model=VerificationResponse)
def verify_email(token: str, db: Session = Depends(get_db)):
    try:
        claims = decode_verification_token(token)
        user_id = int(claims["sub"])
    except (jwt.PyJWTError, KeyError, TypeError, ValueError) as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="invalid or expired verification token",
        ) from exc

    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="user not found",
        )

    if not user.email_verified:
        user.email_verified = True
        db.commit()

    return VerificationResponse(email_verified=user.email_verified)


@router.post("/resend-verification/")
def resend_verification(payload: ResendVerificationRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == payload.email).first()
    if user is None or user.email_verified:
        return {"message": "if the email is registered, a verification link has been sent"}

    now = datetime.now(timezone.utc)
    cooldown_cutoff = now - timedelta(seconds=RESEND_COOLDOWN_SECONDS)
    if (
        user.email_verification_sent_at is not None
        and user.email_verification_sent_at > cooldown_cutoff
    ):
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail="please wait before requesting another verification email",
        )

    token = create_verification_token(user.id, user.email)
    verification_url = f"{settings.API_BASE_URL}/auth/verify-email/?token={token}"

    send_verification_email(user.email, verification_url)

    user.email_verification_sent_at = now
    db.commit()

    return {"message": "if the email is registered, a verification link has been sent"}


@router.post("/refresh/", response_model=SessionResponse)
def refresh(
    payload: RefreshRequest,
    response: Response,
    refresh_token_cookie: str | None = Cookie(default=None, alias="refresh_token"),
    db: Session = Depends(get_db),
):
    raw = _resolve_refresh_token(payload.client_type, payload.refresh_token, refresh_token_cookie)
    if not raw:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="refresh token required",
            headers={"WWW-Authenticate": "Bearer"},
        )

    try:
        user, issued = rotate_refresh_token(db, raw)
    except RefreshTokenReuseError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="refresh token already used, all sessions in this chain were revoked",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc
    except RefreshTokenError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="invalid or expired refresh token",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="this account is deactivated",
        )

    if not user.email_verified:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="please verify your email first",
        )

    return _format_session_response(user, issued, payload.client_type, response)


@router.post("/logout/", status_code=status.HTTP_204_NO_CONTENT)
def logout(
    payload: LogoutRequest,
    response: Response,
    refresh_token_cookie: str | None = Cookie(default=None, alias="refresh_token"),
    db: Session = Depends(get_db),
):
    raw = _resolve_refresh_token(payload.client_type, payload.refresh_token, refresh_token_cookie)
    if raw:
        revoke_refresh_token(db, raw)
    clear_refresh_cookie(response)
    return response


@router.get("/me/", response_model=UserResponse)
def read_me(current_user: User = Depends(get_current_user)):
    return current_user


def _resolve_refresh_token(
    client_type: ClientType,
    body_token: str | None,
    cookie_token: str | None,
) -> str | None:
    if client_type == ClientType.WEB:
        return cookie_token
    return body_token


def _verify_credentials(password: str, user: User | None) -> bool:
    if user is None or user.password_hash is None:
        return False
    return verify_password(password, user.password_hash)


def _build_session(
    user: User,
    db: Session,
    client_type: ClientType,
    response: Response,
) -> SessionResponse:
    access_token = create_access_token(subject=str(user.id), role=user.role.value)
    issued = issue_refresh_token(db, user, client_type)
    return _format_session_response(user, issued, client_type, response, access_token)


def _format_session_response(
    user: User,
    issued: IssuedRefreshToken,
    client_type: ClientType,
    response: Response,
    access_token: str | None = None,
) -> SessionResponse:
    token = access_token or create_access_token(subject=str(user.id), role=user.role.value)
    payload = SessionResponse(
        access_token=token,
        role=user.role,
        is_profile_complete=user.is_profile_complete,
        has_profile_picture=user.has_profile_picture,
    )
    if client_type == ClientType.WEB:
        set_refresh_cookie(response, issued)
    else:
        payload.refresh_token = issued.token
    return payload


def _create_google_user(db: Session, identity: GoogleIdentity) -> User:
    user = User(
        full_name=identity.full_name,
        email=identity.email,
        password_hash=None,
        avatar_url=identity.picture_url,
        google_id=identity.google_id,
        email_verified=True,
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
    if not user.email_verified:
        user.email_verified = True
