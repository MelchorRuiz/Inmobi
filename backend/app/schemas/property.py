from datetime import datetime
from decimal import Decimal
from typing import Optional

from pydantic import BaseModel, ConfigDict, Field, field_validator

from app.models.property import ListingStatus, PropertyType


class PropertyImage(BaseModel):
    url: str
    media_type: str
    display_order: int = 0


class PropertyCreate(BaseModel):
    operation_type: str
    listing_title: str = Field(max_length=150)
    description: Optional[str] = None
    price: Decimal = Field(gt=0, decimal_places=2)
    currency: str = Field(default="MXN", max_length=3)
    maintenance_fee: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    listing_status: ListingStatus = ListingStatus.ACTIVE
    property_type: PropertyType
    street_address: Optional[str] = None
    neighborhood: Optional[str] = None
    postal_code: Optional[str] = None
    city: str
    state_province: str
    country: str = "Mexico"
    latitude: Optional[Decimal] = Field(default=None, ge=-90, le=90, decimal_places=8)
    longitude: Optional[Decimal] = Field(default=None, ge=-180, le=180, decimal_places=8)
    lot_size_sqm: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    built_area_sqm: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    bedrooms: Optional[int] = Field(default=None, ge=0)
    full_bathrooms: Optional[int] = Field(default=None, ge=0)
    half_bathrooms: Optional[int] = Field(default=None, ge=0)
    parking_spaces: Optional[int] = Field(default=None, ge=0)
    stories_floors: Optional[int] = Field(default=None, ge=0)
    property_age_years: Optional[int] = Field(default=None, ge=0)
    amenity_ids: list[int] = []
    images: list[PropertyImage] = []


class PropertyUpdate(BaseModel):
    listing_title: Optional[str] = Field(default=None, max_length=150)
    description: Optional[str] = None
    price: Optional[Decimal] = Field(default=None, gt=0, decimal_places=2)
    currency: Optional[str] = Field(default=None, max_length=3)
    maintenance_fee: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    listing_status: Optional[ListingStatus] = None
    property_type: Optional[PropertyType] = None
    street_address: Optional[str] = None
    neighborhood: Optional[str] = None
    postal_code: Optional[str] = None
    city: Optional[str] = None
    state_province: Optional[str] = None
    country: Optional[str] = None
    latitude: Optional[Decimal] = Field(default=None, ge=-90, le=90, decimal_places=8)
    longitude: Optional[Decimal] = Field(default=None, ge=-180, le=180, decimal_places=8)
    lot_size_sqm: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    built_area_sqm: Optional[Decimal] = Field(default=None, ge=0, decimal_places=2)
    bedrooms: Optional[int] = Field(default=None, ge=0)
    full_bathrooms: Optional[int] = Field(default=None, ge=0)
    half_bathrooms: Optional[int] = Field(default=None, ge=0)
    parking_spaces: Optional[int] = Field(default=None, ge=0)
    stories_floors: Optional[int] = Field(default=None, ge=0)
    property_age_years: Optional[int] = Field(default=None, ge=0)
    amenity_ids: Optional[list[int]] = None
    images: Optional[list[PropertyImage]] = None


class PropertyImageResponse(BaseModel):
    media_id: int
    media_type: str
    file_url: str
    display_order: int

    model_config = ConfigDict(from_attributes=True)


class PriceEstimationResponse(BaseModel):
    estimated_price: Decimal
    min_price: Decimal
    max_price: Decimal
    confidence_score: float
    model_version: str
    operation_type: str

    model_config = ConfigDict(from_attributes=True)

    @field_validator("estimated_price", "min_price", "max_price", mode="before")
    @classmethod
    def convert_to_decimal(cls, v):
        if v is not None and not isinstance(v, Decimal):
            return Decimal(str(v))
        return v


class PropertyResponse(BaseModel):
    property_id: int
    owner_user_id: int
    operation_type_id: int
    operation_name: str
    listing_title: str
    description: Optional[str] = None
    price: Decimal
    currency: str
    maintenance_fee: Optional[Decimal] = None
    listing_status: ListingStatus
    property_type: PropertyType
    street_address: Optional[str] = None
    neighborhood: Optional[str] = None
    postal_code: Optional[str] = None
    city: str
    state_province: str
    country: str
    latitude: Optional[Decimal] = None
    longitude: Optional[Decimal] = None
    lot_size_sqm: Optional[Decimal] = None
    built_area_sqm: Optional[Decimal] = None
    bedrooms: Optional[int] = None
    full_bathrooms: Optional[int] = None
    half_bathrooms: Optional[int] = None
    parking_spaces: Optional[int] = None
    stories_floors: Optional[int] = None
    property_age_years: Optional[int] = None
    published_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
    amenities: list[str] = []
    images: list[PropertyImageResponse] = []
    price_estimation: Optional[PriceEstimationResponse] = None

    model_config = ConfigDict(from_attributes=True)

    @field_validator("price", "maintenance_fee", mode="before")
    @classmethod
    def convert_to_decimal(cls, v):
        if v is not None and not isinstance(v, Decimal):
            return Decimal(str(v))
        return v
