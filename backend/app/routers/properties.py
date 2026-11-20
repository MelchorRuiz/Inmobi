from decimal import Decimal
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.orm import Session

from app.dependencies import get_current_user, require_roles
from app.database import get_db
from app.models.amenity import Amenity
from app.models.operation_type import OperationType
from app.models.property import ListingStatus, Property, PropertyType
from app.models.property_media import PropertyMedia
from app.models.property_price_estimation import PropertyPriceEstimation
from app.models.user import Role, User
from app.schemas.property import PropertyCreate, PropertyResponse, PropertyUpdate
from app.ml.predictor import predict_price


router = APIRouter(prefix="/properties", tags=["Properties"])


@router.post(
    "/",
    response_model=PropertyResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_property(
    property_in: PropertyCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_roles(Role.USER)),
):
    operation = (
        db.query(OperationType)
        .filter(OperationType.operation_name == property_in.operation_type)
        .first()
    )
    if not operation:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Operation type '{property_in.operation_type}' not found",
        )

    property_data = property_in.model_dump(exclude={"operation_type", "amenity_ids", "images"})
    property_data["owner_user_id"] = current_user.id
    property_data["operation_type_id"] = operation.operation_type_id

    db_property = Property(**property_data)

    if property_in.amenity_ids:
        amenities = db.query(Amenity).filter(Amenity.amenity_id.in_(property_in.amenity_ids)).all()
        if len(amenities) != len(property_in.amenity_ids):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="One or more amenity IDs are invalid",
            )
        db_property.amenities = amenities

    db.add(db_property)
    db.flush()

    for idx, img in enumerate(property_in.images):
        media = PropertyMedia(
            property_id=db_property.property_id,
            media_type=img.media_type,
            file_url=img.url,
            display_order=img.display_order or idx,
        )
        db.add(media)

    db.commit()
    db.refresh(db_property)

    estimation = predict_price(db_property)
    if estimation:
        db_estimation = PropertyPriceEstimation(
            property_id=db_property.property_id,
            estimated_price=estimation["estimated_price"],
            min_price=estimation["min_price"],
            max_price=estimation["max_price"],
            confidence_score=estimation["confidence_score"],
            model_version=estimation["model_version"],
            operation_type=estimation["operation_type"],
        )
        db.add(db_estimation)
        db.commit()
        db.refresh(db_property)

    return _to_response(db_property)


@router.get("/", response_model=list[PropertyResponse])
def list_properties(
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    operation_type: Optional[str] = None,
    property_type: Optional[PropertyType] = None,
    listing_status: Optional[ListingStatus] = None,
    city: Optional[str] = None,
    min_price: Optional[Decimal] = None,
    max_price: Optional[Decimal] = None,
    db: Session = Depends(get_db),
):
    query = db.query(Property).join(OperationType)

    if operation_type is not None:
        query = query.filter(OperationType.operation_name == operation_type)
    if property_type is not None:
        query = query.filter(Property.property_type == property_type)
    if listing_status is not None:
        query = query.filter(Property.listing_status == listing_status)
    if city is not None:
        query = query.filter(Property.city.ilike(f"%{city}%"))
    if min_price is not None:
        query = query.filter(Property.price >= min_price)
    if max_price is not None:
        query = query.filter(Property.price <= max_price)

    properties = query.offset(skip).limit(limit).all()
    return [_to_response(p) for p in properties]


@router.get("/my-properties", response_model=list[PropertyResponse])
def my_properties(
    skip: int = Query(0, ge=0),
    limit: int = Query(20, ge=1, le=100),
    operation_type: Optional[str] = None,
    property_type: Optional[PropertyType] = None,
    listing_status: Optional[ListingStatus] = None,
    city: Optional[str] = None,
    min_price: Optional[Decimal] = None,
    max_price: Optional[Decimal] = None,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    query = db.query(Property).join(OperationType).filter(Property.owner_user_id == current_user.id)

    if operation_type is not None:
        query = query.filter(OperationType.operation_name == operation_type)
    if property_type is not None:
        query = query.filter(Property.property_type == property_type)
    if listing_status is not None:
        query = query.filter(Property.listing_status == listing_status)
    if city is not None:
        query = query.filter(Property.city.ilike(f"%{city}%"))
    if min_price is not None:
        query = query.filter(Property.price >= min_price)
    if max_price is not None:
        query = query.filter(Property.price <= max_price)

    properties = query.offset(skip).limit(limit).all()
    return [_to_response(p) for p in properties]


@router.get("/{property_id}", response_model=PropertyResponse)
def get_property(property_id: int, db: Session = Depends(get_db)):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")
    return _to_response(db_property)


@router.put("/{property_id}", response_model=PropertyResponse)
def update_property(
    property_id: int,
    property_in: PropertyUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")
    if db_property.owner_user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail="You can only edit your own properties"
        )

    update_data = property_in.model_dump(exclude_unset=True, exclude={"amenity_ids", "images"})
    for field, value in update_data.items():
        setattr(db_property, field, value)

    if property_in.amenity_ids is not None:
        amenities = db.query(Amenity).filter(Amenity.amenity_id.in_(property_in.amenity_ids)).all()
        if len(amenities) != len(property_in.amenity_ids):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="One or more amenity IDs are invalid",
            )
        db_property.amenities = amenities

    if property_in.images is not None:
        db.query(PropertyMedia).filter(PropertyMedia.property_id == property_id).delete()
        for idx, img in enumerate(property_in.images):
            media = PropertyMedia(
                property_id=property_id,
                media_type=img.media_type,
                file_url=img.url,
                display_order=img.display_order or idx,
            )
            db.add(media)

    db.commit()
    db.refresh(db_property)

    estimation_fields = {
        "property_type",
        "state_province",
        "built_area_sqm",
        "lot_size_sqm",
        "bedrooms",
        "latitude",
        "longitude",
        "currency",
    }
    if any(field in update_data for field in estimation_fields):
        estimation = predict_price(db_property)
        if estimation:
            if db_property.price_estimation:
                db_property.price_estimation.estimated_price = estimation["estimated_price"]
                db_property.price_estimation.min_price = estimation["min_price"]
                db_property.price_estimation.max_price = estimation["max_price"]
                db_property.price_estimation.confidence_score = estimation["confidence_score"]
                db_property.price_estimation.model_version = estimation["model_version"]
                db_property.price_estimation.operation_type = estimation["operation_type"]
            else:
                db_estimation = PropertyPriceEstimation(
                    property_id=db_property.property_id,
                    estimated_price=estimation["estimated_price"],
                    min_price=estimation["min_price"],
                    max_price=estimation["max_price"],
                    confidence_score=estimation["confidence_score"],
                    model_version=estimation["model_version"],
                    operation_type=estimation["operation_type"],
                )
                db.add(db_estimation)
            db.commit()
            db.refresh(db_property)

    return _to_response(db_property)


@router.delete("/{property_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_property(
    property_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_roles(Role.ADMINISTRATOR, Role.USER)),
):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")
    is_owner = db_property.owner_user_id == current_user.id
    is_admin = current_user.role == Role.ADMINISTRATOR
    if not (is_owner or is_admin):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail="You cannot delete this property"
        )
    db.delete(db_property)
    db.commit()


def _to_response(prop: Property) -> PropertyResponse:
    response_data = {c.name: getattr(prop, c.name) for c in prop.__table__.columns}
    response_data["operation_name"] = prop.operation_type.operation_name
    response_data["amenities"] = [a.name for a in prop.amenities]
    response_data["images"] = sorted(prop.media, key=lambda m: m.display_order)
    response_data["price_estimation"] = prop.price_estimation
    return PropertyResponse.model_validate(response_data)
