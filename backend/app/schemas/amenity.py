from pydantic import BaseModel, ConfigDict


class AmenityResponse(BaseModel):
    amenity_id: int
    name: str

    model_config = ConfigDict(from_attributes=True)
