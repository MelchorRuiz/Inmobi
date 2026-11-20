from enum import Enum

from sqlalchemy import Column, DateTime, ForeignKey, Integer, Numeric, String, func
from sqlalchemy.orm import relationship
from app.database import Base


class PropertyType(str, Enum):
    HOUSE = "house"
    APARTMENT = "apartment"
    LAND = "land"
    OFFICE = "office"
    COMMERCIAL_PREMISE = "commercial_premise"
    WAREHOUSE = "warehouse"
    INDUSTRIAL = "industrial"
    PARKING = "parking"


class ListingStatus(str, Enum):
    ACTIVE = "active"
    CANCELLED = "cancelled"
    CLOSED = "closed"


class Property(Base):
    __tablename__ = "tblProperties"

    property_id = Column(Integer, primary_key=True, index=True)
    owner_user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    operation_type_id = Column(
        Integer, ForeignKey("catOperationTypes.operation_type_id"), nullable=False, index=True
    )
    listing_title = Column(String, nullable=False)
    description = Column(String)
    price = Column(Numeric(12, 2), nullable=False)
    currency = Column(String, nullable=False, default="MXN")
    maintenance_fee = Column(Numeric(10, 2))
    listing_status = Column(String, nullable=False, default=ListingStatus.ACTIVE.value, index=True)
    property_type = Column(String, nullable=False, index=True)
    street_address = Column(String)
    neighborhood = Column(String)
    postal_code = Column(String)
    city = Column(String, nullable=False, index=True)
    state_province = Column(String, nullable=False)
    country = Column(String, nullable=False, default="Mexico")
    latitude = Column(Numeric(10, 8))
    longitude = Column(Numeric(11, 8))
    lot_size_sqm = Column(Numeric(10, 2))
    built_area_sqm = Column(Numeric(10, 2))
    bedrooms = Column(Integer)
    full_bathrooms = Column(Integer)
    half_bathrooms = Column(Integer)
    parking_spaces = Column(Integer)
    stories_floors = Column(Integer)
    property_age_years = Column(Integer)
    published_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    owner = relationship("User")
    operation_type = relationship("OperationType")
    amenities = relationship(
        "Amenity", secondary="property_amenities", back_populates="properties"
    )
    media = relationship(
        "PropertyMedia", back_populates="property", cascade="all, delete-orphan"
    )
    price_estimation = relationship(
        "PropertyPriceEstimation", back_populates="property", uselist=False, cascade="all, delete-orphan"
    )
