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
    full_name = Column(String, nullable=False)
    email = Column(String, index=True, nullable=False, unique=True)
    password_hash = Column(String, nullable=False)
    birth_date = Column(Date, nullable=False)
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

    def __repr__(self) -> str:
        return f"<User {self.id} {self.email} {self.role.value}>"
