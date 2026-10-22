import re

from pydantic import BaseModel, ConfigDict, Field, field_validator
from app.models.end_user import Preference

PHONE_SEPARATORS = re.compile(r"[\s\-()]")
PHONE_PATTERN = re.compile(r"^\d{10}$")
COUNTRY_CODE = "52"
NATIONAL_LENGTH = 10


class EndUserBase(BaseModel):
    phone: str = Field(max_length=16)
    privacy_accepted: bool
    preference: Preference


class EndUserCreate(EndUserBase):
    @field_validator("phone", mode="before")
    @classmethod
    def phone_must_be_mexican(cls, value: str) -> str:
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

    @field_validator("privacy_accepted")
    @classmethod
    def privacy_must_be_accepted(cls, value: bool) -> bool:
        if not value:
            raise ValueError("you must accept the privacy notice")
        return value


class EndUserResponse(EndUserBase):
    model_config = ConfigDict(from_attributes=True)
