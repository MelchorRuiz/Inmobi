from enum import Enum

from sqlalchemy import Column, DateTime, Enum as SAEnum, ForeignKey, Integer, String, func
from sqlalchemy.orm import relationship
from app.database import Base


class ClientType(str, Enum):
    WEB = "web"
    MOBILE = "mobile"


class RefreshToken(Base):
    __tablename__ = "refresh_tokens"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    token_hash = Column(String(64), nullable=False, unique=True, index=True)
    family_id = Column(String(22), nullable=False, index=True)
    client_type = Column(
        SAEnum(
            ClientType,
            native_enum=False,
            length=10,
            values_callable=lambda enum: [member.value for member in enum],
        ),
        nullable=False,
    )
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())
    expires_at = Column(DateTime(timezone=True), nullable=False)
    revoked_at = Column(DateTime(timezone=True), nullable=True)

    user = relationship("User")

    def __repr__(self) -> str:
        return f"<RefreshToken {self.id} user={self.user_id} family={self.family_id}>"
