from datetime import datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, Field


class ChatCreate(BaseModel):
    property_id: int


class OtherUserSummary(BaseModel):
    id: int
    full_name: Optional[str] = None
    avatar_url: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class PropertySummary(BaseModel):
    property_id: int
    listing_title: str

    model_config = ConfigDict(from_attributes=True)


class MessageResponse(BaseModel):
    id: int
    sender_id: int
    content: str
    created_at: datetime
    read_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)


class ChatSummary(BaseModel):
    chat_id: int
    other_user: OtherUserSummary
    property: PropertySummary
    last_message: Optional[str] = None
    unread_count: int = 0
    last_message_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)


class ChatDetail(BaseModel):
    chat_id: int
    other_user: OtherUserSummary
    property: PropertySummary
    messages: list[MessageResponse] = []
    unread_count: int = 0

    model_config = ConfigDict(from_attributes=True)


class MessageCreate(BaseModel):
    content: str = Field(min_length=1, max_length=2000)
