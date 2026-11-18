import hashlib
import secrets
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone

from fastapi import Response
from sqlalchemy.orm import Session

from app.config import settings
from app.models.refresh_token import ClientType, RefreshToken
from app.models.user import User

REFRESH_COOKIE_NAME = "refresh_token"
REFRESH_COOKIE_PATH = "/auth"
TOKEN_BYTES = 48
FAMILY_BYTES = 16


class RefreshTokenError(Exception):
    pass


class RefreshTokenReuseError(RefreshTokenError):
    pass


@dataclass(frozen=True)
class IssuedRefreshToken:
    token: str
    max_age: int


def _hash_token(token: str) -> str:
    return hashlib.sha256(token.encode("utf-8")).hexdigest()


def _new_token() -> str:
    return secrets.token_urlsafe(TOKEN_BYTES)


def _new_family_id() -> str:
    return secrets.token_urlsafe(FAMILY_BYTES)


def _max_age() -> int:
    return settings.REFRESH_TOKEN_EXPIRE_DAYS * 24 * 60 * 60


def issue_refresh_token(db: Session, user: User, client_type: ClientType) -> IssuedRefreshToken:
    raw = _new_token()
    record = RefreshToken(
        user_id=user.id,
        token_hash=_hash_token(raw),
        family_id=_new_family_id(),
        client_type=client_type,
        expires_at=datetime.now(timezone.utc) + timedelta(days=settings.REFRESH_TOKEN_EXPIRE_DAYS),
    )
    db.add(record)
    db.commit()
    return IssuedRefreshToken(token=raw, max_age=_max_age())


def rotate_refresh_token(db: Session, raw: str) -> tuple[User, IssuedRefreshToken]:
    now = datetime.now(timezone.utc)
    token_hash = _hash_token(raw)
    
    record = (
        db.query(RefreshToken)
        .filter(RefreshToken.token_hash == token_hash)
        .first()
    )

    if record is None:
        raise RefreshTokenError("unknown refresh token")

    if record.revoked_at is not None:
        _revoke_family(db, record.family_id)
        raise RefreshTokenReuseError("refresh token already used, family revoked")

    expired = (
        db.query(RefreshToken.id)
        .filter(
            RefreshToken.token_hash == token_hash,
            RefreshToken.expires_at <= now
        )
        .first()
    )

    if expired is not None:
        record.revoked_at = now
        db.commit()
        raise RefreshTokenError("refresh token expired")

    record.revoked_at = now

    new_raw = _new_token()
    successor = RefreshToken(
        user_id=record.user_id,
        token_hash=_hash_token(new_raw),
        family_id=record.family_id,
        client_type=record.client_type,
        expires_at=now + timedelta(days=settings.REFRESH_TOKEN_EXPIRE_DAYS),
    )
    db.add(successor)
    db.commit()

    return record.user, IssuedRefreshToken(token=new_raw, max_age=_max_age())


def _revoke_family(db: Session, family_id: str) -> None:
    now = datetime.now(timezone.utc)
    (
        db.query(RefreshToken)
        .filter(RefreshToken.family_id == family_id, RefreshToken.revoked_at.is_(None))
        .update({RefreshToken.revoked_at: now}, synchronize_session=False)
    )


def revoke_refresh_token(db: Session, raw: str) -> None:
    record = (
        db.query(RefreshToken)
        .filter(RefreshToken.token_hash == _hash_token(raw))
        .first()
    )
    if record is None:
        return
    _revoke_family(db, record.family_id)
    db.commit()


def set_refresh_cookie(response: Response, issued: IssuedRefreshToken) -> None:
    response.set_cookie(
        key=REFRESH_COOKIE_NAME,
        value=issued.token,
        max_age=issued.max_age,
        httponly=True,
        secure=settings.refresh_cookie_secure,
        samesite=settings.refresh_cookie_samesite,
        path=REFRESH_COOKIE_PATH,
    )


def clear_refresh_cookie(response: Response) -> None:
    response.delete_cookie(
        key=REFRESH_COOKIE_NAME,
        path=REFRESH_COOKIE_PATH,
        secure=settings.refresh_cookie_secure,
        samesite=settings.refresh_cookie_samesite,
    )
