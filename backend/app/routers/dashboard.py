from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import get_current_user
from app.models.property import ListingStatus, Property
from app.models.property_step import PropertyStep
from app.models.user import User
from app.routers.properties import _to_response
from app.schemas.dashboard import DashboardResponse


router = APIRouter(prefix="/dashboard", tags=["Dashboard"])


@router.get("/", response_model=DashboardResponse)
def get_dashboard(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    interested_property_ids = (
        db.query(PropertyStep.property_id)
        .filter(PropertyStep.user_id == current_user.id)
        .distinct()
        .all()
    )
    interested_property_ids = [pid[0] for pid in interested_property_ids]

    interested_properties = []
    if interested_property_ids:
        interested_properties = (
            db.query(Property)
            .filter(
                Property.property_id.in_(interested_property_ids),
                Property.listing_status == ListingStatus.ACTIVE.value
            )
            .all()
        )

    my_published_properties = (
        db.query(Property)
        .filter(
            Property.owner_user_id == current_user.id,
            Property.listing_status == ListingStatus.ACTIVE.value
        )
        .all()
    )

    return DashboardResponse(
        interested_properties=[_to_response(p) for p in interested_properties],
        my_published_properties=[_to_response(p) for p in my_published_properties],
    )
