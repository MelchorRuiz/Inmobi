from datetime import datetime, timedelta, timezone

import bcrypt
import jwt
from app.config import settings

ALGORITHM = "HS256"
BCRYPT_ROUNDS = 12
VERIFICATION_PURPOSE = "email_verification"


def hash_password(password: str) -> str:
    salt = bcrypt.gensalt(rounds=BCRYPT_ROUNDS)
    return bcrypt.hashpw(password.encode("utf-8"), salt).decode("utf-8")


def verify_password(password: str, password_hash: str) -> bool:
    try:
        return bcrypt.checkpw(password.encode("utf-8"), password_hash.encode("utf-8"))
    except ValueError:
        return False


def create_access_token(subject: str, role: str, expires_delta: timedelta | None = None) -> str:
    expires_at = datetime.now(timezone.utc) + (
        expires_delta or timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    )
    payload = {"sub": subject, "role": role, "exp": expires_at}
    return jwt.encode(payload, settings.SECRET_KEY, algorithm=ALGORITHM)


def decode_access_token(token: str) -> dict:
    return jwt.decode(token, settings.SECRET_KEY, algorithms=[ALGORITHM])


def create_verification_token(user_id: int, email: str) -> str:
    expires_at = datetime.now(timezone.utc) + timedelta(
        hours=settings.EMAIL_VERIFICATION_TOKEN_EXPIRE_HOURS,
    )
    payload = {
        "sub": str(user_id),
        "email": email,
        "purpose": VERIFICATION_PURPOSE,
        "exp": expires_at,
    }
    return jwt.encode(payload, settings.SECRET_KEY, algorithm=ALGORITHM)


def decode_verification_token(token: str) -> dict:
    claims = jwt.decode(token, settings.SECRET_KEY, algorithms=[ALGORITHM])
    if claims.get("purpose") != VERIFICATION_PURPOSE:
        raise jwt.InvalidTokenError("invalid token purpose")
    return claims
