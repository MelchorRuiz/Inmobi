from enum import Enum

from sqlalchemy import Boolean, Column, Date, DateTime, Enum as SAEnum, Integer, String, func
from sqlalchemy.orm import relationship
from app.database import Base


class Role(str, Enum):
    ADMINISTRATOR = "administrator"
    USER = "user"
    LEGAL_STAFF = "legal_staff"


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String, nullable=True)
    email = Column(String, index=True, nullable=False, unique=True)
    password_hash = Column(String, nullable=True)
    birth_date = Column(Date, nullable=True)
    avatar_url = Column(String, nullable=True)
    google_id = Column(String, nullable=True, index=True, unique=True)
    email_verified = Column(Boolean, nullable=False, default=False)
    email_verification_sent_at = Column(DateTime(timezone=True), nullable=True)
    role = Column(
        SAEnum(
            Role,
            native_enum=False,
            length=20,
            values_callable=lambda enum: [member.value for member in enum],
        ),
        nullable=False,
        default=Role.USER,
    )
    is_active = Column(Boolean, nullable=False, default=True)
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())

    end_user = relationship("EndUser", back_populates="user", uselist=False)
    legal_staff = relationship(
        "LegalStaff",
        back_populates="user",
        uselist=False,
        cascade="all, delete-orphan",
    )

    @property
    def is_profile_complete(self) -> bool:
        end_user = self.end_user
        return bool(
            self.full_name
            and self.birth_date
            and end_user
            and end_user.phone
            and end_user.preference
            and end_user.privacy_accepted
        )

    @property
    def has_profile_picture(self) -> bool:
        return bool(self.avatar_url)

    @property
    def has_password(self) -> bool:
        return self.password_hash is not None

    def __repr__(self) -> str:
        return f"<User {self.id} {self.email} {self.role.value}>"
