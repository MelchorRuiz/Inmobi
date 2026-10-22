from enum import Enum

from sqlalchemy import Boolean, Column, Enum as SAEnum, ForeignKey, String
from sqlalchemy.orm import relationship
from app.database import Base


class Preference(str, Enum):
    RENT = "rent"
    SELL = "sell"
    BUY = "buy"
    LEASE = "lease"


class EndUser(Base):
    __tablename__ = "end_users"

    user_id = Column(
        ForeignKey("users.id", ondelete="CASCADE"),
        primary_key=True,
        index=True,
        autoincrement=False,
    )
    phone = Column(String, nullable=False, unique=True)
    privacy_accepted = Column(Boolean, nullable=False, default=False)
    preference = Column(
        SAEnum(
            Preference,
            native_enum=False,
            length=20,
            values_callable=lambda enum: [member.value for member in enum],
        ),
        nullable=False,
    )

    user = relationship("User", back_populates="end_user")

    def __repr__(self) -> str:
        return f"<EndUser {self.user_id} {self.phone}>"
