from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict, Field


class LegalAdviceRequestCreate(BaseModel):
    property_id: int
    description: str = Field(min_length=10, max_length=1000)


class LegalAdviceRequestResponse(BaseModel):
    id: int
    user_id: int
    property_id: int
    description: str
    is_attended: bool
    created_at: datetime
    model_config = ConfigDict(from_attributes=True)


class AcceptLegalAdviceResponse(LegalAdviceRequestResponse):
    chat_id: int


class InterestedPropertyItem(BaseModel):
    property_id: int
    listing_title: str
    images: list[str]
    has_legal_advice_request: bool


class LegalAdviceRequestListItem(BaseModel):
    id: int
    user_id: int
    user_full_name: Optional[str] = None
    user_email: str
    property_id: int
    property_title: str
    description: str
    is_attended: bool
    created_at: datetime
