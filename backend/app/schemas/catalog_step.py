from datetime import datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict


class CatalogStepResponse(BaseModel):
    step_id: int
    operation_type_id: int
    title: str
    description: str
    step_order: int
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None

    model_config = ConfigDict(from_attributes=True)
