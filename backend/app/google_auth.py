from dataclasses import dataclass
from functools import lru_cache
from typing import Any

import jwt

GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs"
GOOGLE_ISSUERS = frozenset({"https://accounts.google.com", "accounts.google.com"})


@dataclass
class GoogleIdentity:
    google_id: str
    email: str
    full_name: str | None
    picture_url: str | None


@lru_cache
def _jwk_client() -> jwt.PyJWKClient:
    return jwt.PyJWKClient(GOOGLE_JWKS_URL, cache_keys=True)


def verify_google_id_token(id_token: str, audience: str) -> GoogleIdentity:
    client = _jwk_client()
    signing_key = client.get_signing_key_from_jwt(id_token)

    claims: dict[str, Any] = jwt.decode(
        id_token,
        signing_key.key,
        algorithms=["RS256"],
        audience=audience,
        issuer=list(GOOGLE_ISSUERS),
        options={"require": ["exp", "iat", "aud", "iss", "sub"]},
    )

    google_id = claims.get("sub")
    email = claims.get("email")
    if not google_id or not email:
        raise jwt.InvalidTokenError("google token missing required claims")

    if claims.get("email_verified") is not True:
        raise jwt.InvalidTokenError("google email is not verified")

    return GoogleIdentity(
        google_id=str(google_id),
        email=str(email).strip().lower(),
        full_name=_as_optional_str(claims.get("name")),
        picture_url=_as_optional_str(claims.get("picture")),
    )


def _as_optional_str(value: Any) -> str | None:
    if value is None:
        return None
    text = str(value).strip()
    return text or None
