from datetime import date, datetime
from typing import Annotated, Any

from pydantic import AfterValidator, BaseModel, ConfigDict, EmailStr, Field
from app.models.end_user import Preference
from app.models.refresh_token import ClientType
from app.models.user import Role
from app.schemas.end_user import EndUserCreate, EndUserResponse, PhoneNumber

MINIMUM_AGE = 18


def _normalize_email(value: Any) -> str:
    return str(value).strip().lower()


def _validate_birth_date(value: date) -> date:
    today = date.today()
    if value > today:
        raise ValueError("date of birth cannot be in the future")
    age = today.year - value.year - ((today.month, today.day) < (value.month, value.day))
    if age < MINIMUM_AGE:
        raise ValueError(f"you must be at least {MINIMUM_AGE} years old")
    return value


NormalizedEmail = Annotated[EmailStr, AfterValidator(_normalize_email)]
ValidBirthDate = Annotated[date, AfterValidator(_validate_birth_date)]


class UserBase(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: NormalizedEmail
    birth_date: ValidBirthDate


class UserRegister(UserBase, EndUserCreate):
    password: str = Field(min_length=8, max_length=72)


class UserProfileUpdate(BaseModel):
    full_name: str | None = Field(default=None, min_length=2, max_length=120)
    birth_date: ValidBirthDate | None = None
    phone: PhoneNumber | None = None
    privacy_accepted: bool | None = None
    preference: Preference | None = None


class UserLogin(BaseModel):
    email: NormalizedEmail
    password: str
    client_type: ClientType


class UserResponse(BaseModel):
    id: int
    full_name: str | None = None
    email: EmailStr
    birth_date: date | None = None
    avatar_url: str | None = None
    role: Role
    is_active: bool
    created_at: datetime
    end_user: EndUserResponse | None = None

    model_config = ConfigDict(from_attributes=True)


class GoogleLogin(BaseModel):
    id_token: str
    client_type: ClientType


class RefreshRequest(BaseModel):
    client_type: ClientType
    refresh_token: str | None = None


class LogoutRequest(BaseModel):
    client_type: ClientType
    refresh_token: str | None = None


class SessionResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    refresh_token: str | None = None
    role: Role
    is_profile_complete: bool
    has_profile_picture: bool
