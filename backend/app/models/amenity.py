from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from app.database import Base


class Amenity(Base):
    __tablename__ = "catAmenities"

    amenity_id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False, unique=True)

    properties = relationship(
        "Property", secondary="property_amenities", back_populates="amenities"
    )
