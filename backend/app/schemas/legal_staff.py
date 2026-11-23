from datetime import datetime

from typing import Annotated, Any

from pydantic import AfterValidator, BaseModel, ConfigDict, EmailStr, Field

from app.schemas.end_user import PhoneNumber


def _normalize_email(value: Any) -> str:
    return str(value).strip().lower()


NormalizedEmail = Annotated[EmailStr, AfterValidator(_normalize_email)]


class LegalStaffCreate(BaseModel):
    email: NormalizedEmail
    password: str = Field(min_length=8, max_length=72)
    full_name: str = Field(min_length=2, max_length=120)
    id_number: str = Field(min_length=1, max_length=40)
    specialty: str = Field(min_length=2, max_length=120)
    firm_name: str | None = Field(default=None, max_length=120)
    phone: PhoneNumber | None = None


class LegalStaffUpdate(BaseModel):
    password: str | None = Field(default=None, min_length=8, max_length=72)
    full_name: str | None = Field(default=None, min_length=2, max_length=120)
    id_number: str | None = Field(default=None, min_length=1, max_length=40)
    specialty: str | None = Field(default=None, min_length=2, max_length=120)
    firm_name: str | None = Field(default=None, max_length=120)
    phone: PhoneNumber | None = None


class LegalStaffResponse(BaseModel):
    id_number: str
    specialty: str
    firm_name: str | None = None
    phone: str | None = None

    model_config = ConfigDict(from_attributes=True)


class LegalStaffUserResponse(BaseModel):
    id: int
    email: EmailStr
    full_name: str | None = None
    is_active: bool
    created_at: datetime
    legal_staff: LegalStaffResponse

    model_config = ConfigDict(from_attributes=True)
