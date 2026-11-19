from sqlalchemy import Column, ForeignKey, Integer, Table
from app.database import Base

property_amenities = Table(
    "property_amenities",
    Base.metadata,
    Column("property_id", Integer, ForeignKey("tblProperties.property_id", ondelete="CASCADE"), primary_key=True),
    Column("amenity_id", Integer, ForeignKey("catAmenities.amenity_id", ondelete="CASCADE"), primary_key=True),
)
