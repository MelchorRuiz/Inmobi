from pydantic import BaseModel

from app.schemas.property import PropertyResponse


class DashboardResponse(BaseModel):
    interested_properties: list[PropertyResponse]
    my_published_properties: list[PropertyResponse]
