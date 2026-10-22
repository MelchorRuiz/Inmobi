from datetime import date, datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator
from app.models.user import Role
from app.schemas.end_user import EndUserCreate, EndUserResponse

MINIMUM_AGE = 18


class UserBase(BaseModel):
    full_name: str = Field(min_length=2, max_length=120)
    email: EmailStr
    birth_date: date


class UserRegister(UserBase, EndUserCreate):
    password: str = Field(min_length=8, max_length=72)

    @field_validator("email", mode="before")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        return str(value).strip().lower()

    @field_validator("birth_date")
    @classmethod
    def birth_date_must_be_valid(cls, value: date) -> date:
        today = date.today()
        if value > today:
            raise ValueError("date of birth cannot be in the future")
        age = today.year - value.year - ((today.month, today.day) < (value.month, value.day))
        if age < MINIMUM_AGE:
            raise ValueError(f"you must be at least {MINIMUM_AGE} years old")
        return value


class UserLogin(BaseModel):
    email: EmailStr
    password: str

    @field_validator("email", mode="before")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        return str(value).strip().lower()


class UserResponse(BaseModel):
    id: int
    full_name: str
    email: EmailStr
    birth_date: date
    role: Role
    is_active: bool
    created_at: datetime
    end_user: EndUserResponse | None = None

    model_config = ConfigDict(from_attributes=True)


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
