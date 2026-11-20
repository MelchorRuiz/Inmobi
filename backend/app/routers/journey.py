from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import get_current_user
from app.models.catalog_step import CatalogStep
from app.models.operation_type import OperationType
from app.models.property import Property
from app.models.property_step import PropertyStep
from app.models.user import User
from app.schemas.property_step import (
    PropertyProgressResponse,
    PropertyStepResponse,
    PropertyStepUpdate,
)

router = APIRouter(prefix="/journey", tags=["Journey"])


@router.post("/properties/{property_id}/interest", status_code=status.HTTP_201_CREATED)
def show_interest(
    property_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")

    existing = (
        db.query(PropertyStep)
        .filter(PropertyStep.user_id == current_user.id, PropertyStep.property_id == property_id)
        .first()
    )
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="You already have interest in this property",
        )

    catalog_steps = (
        db.query(CatalogStep)
        .filter(CatalogStep.operation_type_id == db_property.operation_type_id)
        .order_by(CatalogStep.step_order)
        .all()
    )

    if not catalog_steps:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No catalog steps found for this operation type",
        )

    for step in catalog_steps:
        property_step = PropertyStep(
            user_id=current_user.id,
            property_id=property_id,
            step_id=step.step_id,
            completed=False,
            comments=None,
        )
        db.add(property_step)

    db.commit()

    return {"message": "Interest registered successfully", "steps_count": len(catalog_steps)}


@router.delete("/properties/{property_id}/interest", status_code=status.HTTP_204_NO_CONTENT)
def remove_interest(
    property_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")

    existing = (
        db.query(PropertyStep)
        .filter(PropertyStep.user_id == current_user.id, PropertyStep.property_id == property_id)
        .first()
    )
    if not existing:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="You don't have interest in this property",
        )

    db.query(PropertyStep).filter(
        PropertyStep.user_id == current_user.id, PropertyStep.property_id == property_id
    ).delete(synchronize_session=False)

    db.commit()


@router.get("/properties/{property_id}/progress", response_model=PropertyProgressResponse)
def get_progress(
    property_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_property = db.query(Property).filter(Property.property_id == property_id).first()
    if not db_property:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Property not found")

    property_steps = (
        db.query(PropertyStep)
        .join(CatalogStep)
        .filter(
            PropertyStep.user_id == current_user.id,
            PropertyStep.property_id == property_id,
        )
        .order_by(CatalogStep.step_order)
        .all()
    )

    if not property_steps:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No progress found for this property",
        )

    total_steps = len(property_steps)
    completed_steps = sum(1 for ps in property_steps if ps.completed)

    steps_response = []
    for ps in property_steps:
        step_data = {
            "id": ps.id,
            "user_id": ps.user_id,
            "property_id": ps.property_id,
            "step_id": ps.step_id,
            "completed": ps.completed,
            "comments": ps.comments,
            "completed_at": ps.completed_at,
            "created_at": ps.created_at,
            "updated_at": ps.updated_at,
            "title": ps.step.title,
            "description": ps.step.description,
            "step_order": ps.step.step_order,
        }
        steps_response.append(PropertyStepResponse.model_validate(step_data))

    progress_percentage = (completed_steps / total_steps * 100) if total_steps > 0 else 0

    return PropertyProgressResponse(
        property_id=property_id,
        total_steps=total_steps,
        completed_steps=completed_steps,
        progress_percentage=round(progress_percentage, 2),
        steps=steps_response,
    )


@router.put(
    "/properties/{property_id}/steps/{step_id}",
    response_model=PropertyStepResponse,
)
def update_step(
    property_id: int,
    step_id: int,
    step_update: PropertyStepUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    property_step = (
        db.query(PropertyStep)
        .filter(
            PropertyStep.user_id == current_user.id,
            PropertyStep.property_id == property_id,
            PropertyStep.step_id == step_id,
        )
        .first()
    )

    if not property_step:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Step not found")

    if step_update.completed is not None:
        property_step.completed = step_update.completed
        if step_update.completed:
            from datetime import datetime, timezone
            property_step.completed_at = datetime.now(timezone.utc)
        else:
            property_step.completed_at = None

    if step_update.comments is not None:
        property_step.comments = step_update.comments

    db.commit()
    db.refresh(property_step)

    step_data = {
        "id": property_step.id,
        "user_id": property_step.user_id,
        "property_id": property_step.property_id,
        "step_id": property_step.step_id,
        "completed": property_step.completed,
        "comments": property_step.comments,
        "completed_at": property_step.completed_at,
        "created_at": property_step.created_at,
        "updated_at": property_step.updated_at,
        "title": property_step.step.title,
        "description": property_step.step.description,
        "step_order": property_step.step.step_order,
    }

    return PropertyStepResponse.model_validate(step_data)
