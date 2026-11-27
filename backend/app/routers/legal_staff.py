from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import require_roles
from app.models.end_user import EndUser
from app.models.legal_staff import LegalStaff
from app.models.user import Role, User
from app.schemas.legal_staff import (
    LegalStaffCreate,
    LegalStaffResponse,
    LegalStaffSelfUpdate,
    LegalStaffUpdate,
    LegalStaffUserResponse,
)
from app.schemas.user import UserResponse
from app.security import hash_password

router = APIRouter(prefix="/legal-staff", tags=["Legal Staff"])

admin_only = require_roles(Role.ADMINISTRATOR)
legal_staff_only = require_roles(Role.LEGAL_STAFF)


@router.patch("/me/", response_model=UserResponse)
def update_my_legal_staff_profile(
    payload: LegalStaffSelfUpdate,
    current_user: User = Depends(legal_staff_only),
    db: Session = Depends(get_db),
):
    data = payload.model_dump(exclude_unset=True)

    legal_data = {
        key: data.pop(key)
        for key in ("id_number", "specialty", "firm_name", "phone")
        if key in data
    }

    if "id_number" in legal_data and legal_data["id_number"] is not None:
        conflict = (
            db.query(LegalStaff)
            .filter(
                LegalStaff.id_number == legal_data["id_number"],
                LegalStaff.user_id != current_user.id,
            )
            .first()
        )
        if conflict:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="id number is already registered",
            )

    if "phone" in legal_data:
        new_phone = legal_data["phone"]
        if new_phone is not None:
            conflict = db.query(EndUser).filter(EndUser.phone == new_phone).first()
            if conflict and conflict.user_id != current_user.id:
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="phone number is already registered",
                )

    for key in ("full_name",):
        if key in data:
            setattr(current_user, key, data[key])

    for key, value in legal_data.items():
        setattr(current_user.legal_staff, key, value)

    db.commit()
    db.refresh(current_user)
    return current_user


@router.post(
    "/",
    response_model=LegalStaffUserResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_legal_staff(
    payload: LegalStaffCreate,
    db: Session = Depends(get_db),
    _admin: User = Depends(admin_only),
):
    if db.query(User).filter(User.email == payload.email).first():
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="email is already registered",
        )

    if db.query(LegalStaff).filter(LegalStaff.id_number == payload.id_number).first():
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="id number is already registered",
        )

    if payload.phone and db.query(EndUser).filter(EndUser.phone == payload.phone).first():
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="phone number is already registered",
        )

    user = User(
        email=payload.email,
        password_hash=hash_password(payload.password),
        full_name=payload.full_name,
        role=Role.LEGAL_STAFF,
        email_verified=True,
        is_active=True,
    )
    db.add(user)
    db.flush()

    db.add(
        LegalStaff(
            user_id=user.id,
            id_number=payload.id_number,
            specialty=payload.specialty,
            firm_name=payload.firm_name,
            phone=payload.phone,
        )
    )
    db.commit()
    db.refresh(user)
    return user


@router.get("/", response_model=list[LegalStaffUserResponse])
def list_legal_staff(
    db: Session = Depends(get_db),
    _admin: User = Depends(admin_only),
):
    users = (
        db.query(User)
        .join(LegalStaff)
        .filter(User.role == Role.LEGAL_STAFF)
        .order_by(User.id)
        .all()
    )
    return users


@router.get("/{user_id}/", response_model=LegalStaffUserResponse)
def get_legal_staff(
    user_id: int,
    db: Session = Depends(get_db),
    _admin: User = Depends(admin_only),
):
    user = db.query(User).filter(User.id == user_id, User.role == Role.LEGAL_STAFF).first()
    if not user or not user.legal_staff:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="legal staff member not found",
        )
    return user


@router.patch("/{user_id}/", response_model=LegalStaffUserResponse)
def update_legal_staff(
    user_id: int,
    payload: LegalStaffUpdate,
    db: Session = Depends(get_db),
    _admin: User = Depends(admin_only),
):
    user = db.query(User).filter(User.id == user_id, User.role == Role.LEGAL_STAFF).first()
    if not user or not user.legal_staff:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="legal staff member not found",
        )

    data = payload.model_dump(exclude_unset=True)
    legal_data = {
        key: data.pop(key)
        for key in ("id_number", "specialty", "firm_name", "phone")
        if key in data
    }

    if "id_number" in legal_data and legal_data["id_number"] is not None:
        conflict = (
            db.query(LegalStaff)
            .filter(LegalStaff.id_number == legal_data["id_number"], LegalStaff.user_id != user_id)
            .first()
        )
        if conflict:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="id number is already registered",
            )

    if "phone" in legal_data:
        new_phone = legal_data["phone"]
        if new_phone is not None:
            conflict = db.query(EndUser).filter(EndUser.phone == new_phone).first()
            if conflict and conflict.user_id != user_id:
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="phone number is already registered",
                )

    if "password" in data:
        user.password_hash = hash_password(data.pop("password"))

    for key in ("full_name",):
        if key in data:
            setattr(user, key, data[key])

    for key, value in legal_data.items():
        setattr(user.legal_staff, key, value)

    db.commit()
    db.refresh(user)
    return user


@router.delete("/{user_id}/", status_code=status.HTTP_204_NO_CONTENT)
def delete_legal_staff(
    user_id: int,
    db: Session = Depends(get_db),
    _admin: User = Depends(admin_only),
):
    user = db.query(User).filter(User.id == user_id, User.role == Role.LEGAL_STAFF).first()
    if not user or not user.legal_staff:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="legal staff member not found",
        )

    db.delete(user)
    db.commit()
