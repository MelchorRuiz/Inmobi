from datetime import datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict, Field


class PropertyStepUpdate(BaseModel):
    completed: Optional[bool] = None
    comments: Optional[str] = Field(default=None, max_length=1000)


class PropertyStepResponse(BaseModel):
    id: int
    user_id: int
    property_id: int
    step_id: int
    completed: bool
    comments: Optional[str] = None
    completed_at: Optional[datetime] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
    title: str
    description: str
    step_order: int

    model_config = ConfigDict(from_attributes=True)


class PropertyProgressResponse(BaseModel):
    property_id: int
    total_steps: int
    completed_steps: int
    progress_percentage: float
    steps: list[PropertyStepResponse]
