import re
from typing import Annotated, Any

from pydantic import AfterValidator, BaseModel, ConfigDict, Field, field_validator
from app.models.end_user import Preference

PHONE_SEPARATORS = re.compile(r"[\s\-()]")
PHONE_PATTERN = re.compile(r"^\d{10}$")
COUNTRY_CODE = "52"
NATIONAL_LENGTH = 10


def _normalize_phone(value: Any) -> str:
    digits = PHONE_SEPARATORS.sub("", str(value).strip())
    if digits.startswith("00"):
        digits = digits[2:]
    if digits.startswith("+"):
        digits = digits[1:]
    if len(digits) == NATIONAL_LENGTH + len(COUNTRY_CODE) and digits.startswith(COUNTRY_CODE):
        digits = digits[len(COUNTRY_CODE):]
    if not PHONE_PATTERN.match(digits):
        raise ValueError("phone number must have 10 digits, for example 5512345678")
    return digits


PhoneNumber = Annotated[str, AfterValidator(_normalize_phone), Field(max_length=16)]


class EndUserCreate(BaseModel):
    phone: PhoneNumber
    privacy_accepted: bool
    preference: Preference

    @field_validator("privacy_accepted")
    @classmethod
    def privacy_must_be_accepted(cls, value: bool) -> bool:
        if not value:
            raise ValueError("you must accept the privacy notice")
        return value


class EndUserUpdate(BaseModel):
    phone: PhoneNumber | None = None
    privacy_accepted: bool | None = None
    preference: Preference | None = None


class EndUserResponse(BaseModel):
    phone: str | None = None
    privacy_accepted: bool | None = None
    preference: Preference | None = None

    model_config = ConfigDict(from_attributes=True)
