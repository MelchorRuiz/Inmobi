from sqlalchemy import Boolean, Column, DateTime, ForeignKey, Integer, String, UniqueConstraint, func
from sqlalchemy.orm import relationship
from app.database import Base


class LegalAdviceRequest(Base):
    __tablename__ = "legal_advice_requests"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    property_id = Column(Integer, ForeignKey("tblProperties.property_id", ondelete="CASCADE"),
                         nullable=False, index=True)
    description = Column(String, nullable=False)
    is_attended = Column(Boolean, nullable=False, default=False)
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())

    user = relationship("User")
    property = relationship("Property")

    __table_args__ = (
        UniqueConstraint("user_id", "property_id", name="uq_user_property_legal_advice"),
    )

    def __repr__(self) -> str:
        return f"<LegalAdviceRequest {self.id} user={self.user_id} property={self.property_id}>"
