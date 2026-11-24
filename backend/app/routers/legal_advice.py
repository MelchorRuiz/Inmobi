from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import get_current_user, require_roles
from app.models.chat import Chat
from app.models.legal_advice_request import LegalAdviceRequest
from app.models.property import Property
from app.models.property_media import PropertyMedia
from app.models.property_step import PropertyStep
from app.models.user import Role, User
from app.schemas.legal_advice import (
    AcceptLegalAdviceResponse,
    InterestedPropertyItem,
    LegalAdviceRequestCreate,
    LegalAdviceRequestListItem,
    LegalAdviceRequestResponse,
)

router = APIRouter(prefix="/legal-advice", tags=["Legal Advice"])

RequireUser = require_roles(Role.USER)
RequireLegalStaff = require_roles(Role.LEGAL_STAFF)


@router.post("/requests/", response_model=LegalAdviceRequestResponse,
             status_code=status.HTTP_201_CREATED)
def create_legal_advice_request(
    payload: LegalAdviceRequestCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(RequireUser),
):
    db_property = db.get(Property, payload.property_id)
    if db_property is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="property not found",
        )

    interest = (
        db.query(PropertyStep)
        .filter(
            PropertyStep.user_id == current_user.id,
            PropertyStep.property_id == payload.property_id,
        )
        .first()
    )
    if interest is None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="you must show interest in the property before requesting legal advice",
        )

    existing = (
        db.query(LegalAdviceRequest)
        .filter(
            LegalAdviceRequest.user_id == current_user.id,
            LegalAdviceRequest.property_id == payload.property_id,
        )
        .first()
    )
    if existing is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="you already have a legal advice request for this property",
        )

    request = LegalAdviceRequest(
        user_id=current_user.id,
        property_id=payload.property_id,
        description=payload.description,
    )
    db.add(request)
    db.commit()
    db.refresh(request)

    return LegalAdviceRequestResponse.model_validate(request)


@router.get("/interested-properties/", response_model=list[InterestedPropertyItem])
def list_interested_properties(
    db: Session = Depends(get_db),
    current_user: User = Depends(RequireUser),
):
    interested_property_ids = (
        db.query(PropertyStep.property_id)
        .filter(PropertyStep.user_id == current_user.id)
        .distinct()
        .all()
    )
    property_ids = [pid for (pid,) in interested_property_ids]

    if not property_ids:
        return []

    properties = db.query(Property).filter(Property.property_id.in_(property_ids)).all()

    properties_with_advice = (
        db.query(LegalAdviceRequest.property_id)
        .filter(
            LegalAdviceRequest.user_id == current_user.id,
            LegalAdviceRequest.property_id.in_(property_ids),
        )
        .all()
    )
    advised_ids = {pid for (pid,) in properties_with_advice}

    result = []
    for prop in properties:
        images = (
            db.query(PropertyMedia.file_url)
            .filter(
                PropertyMedia.property_id == prop.property_id,
                PropertyMedia.media_type == "image",
            )
            .order_by(PropertyMedia.display_order)
            .all()
        )
        image_urls = [url for (url,) in images]

        result.append(
            InterestedPropertyItem(
                property_id=prop.property_id,
                listing_title=prop.listing_title,
                images=image_urls,
                has_legal_advice_request=prop.property_id in advised_ids,
            )
        )

    return result


@router.get("/requests/", response_model=list[LegalAdviceRequestListItem])
def list_legal_advice_requests(
    db: Session = Depends(get_db),
    current_user: User = Depends(RequireLegalStaff),
):
    requests = (
        db.query(LegalAdviceRequest)
        .order_by(LegalAdviceRequest.created_at.desc())
        .all()
    )

    return [
        LegalAdviceRequestListItem(
            id=r.id,
            user_id=r.user_id,
            user_full_name=r.user.full_name,
            user_email=r.user.email,
            property_id=r.property_id,
            property_title=r.property.listing_title,
            description=r.description,
            is_attended=r.is_attended,
            created_at=r.created_at,
        )
        for r in requests
    ]


@router.post(
    "/requests/{request_id}/accept/",
    response_model=AcceptLegalAdviceResponse,
)
def accept_legal_advice_request(
    request_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(RequireLegalStaff),
):
    request = db.get(LegalAdviceRequest, request_id)
    if request is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="legal advice request not found",
        )

    if request.is_attended:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="this request has already been attended",
        )

    request.is_attended = True

    user_1_id = min(current_user.id, request.user_id)
    user_2_id = max(current_user.id, request.user_id)

    existing_chat = (
        db.query(Chat)
        .filter(
            Chat.property_id == request.property_id,
            Chat.user_1_id == user_1_id,
            Chat.user_2_id == user_2_id,
        )
        .first()
    )

    if existing_chat is None:
        chat = Chat(
            property_id=request.property_id,
            user_1_id=user_1_id,
            user_2_id=user_2_id,
        )
        db.add(chat)
        db.commit()
        db.refresh(chat)
    else:
        chat = existing_chat

    db.refresh(request)

    return AcceptLegalAdviceResponse(
        id=request.id,
        user_id=request.user_id,
        property_id=request.property_id,
        description=request.description,
        is_attended=request.is_attended,
        created_at=request.created_at,
        chat_id=chat.id,
    )
