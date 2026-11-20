from sqlalchemy import Boolean, Column, DateTime, ForeignKey, Integer, String, UniqueConstraint, func
from sqlalchemy.orm import relationship
from app.database import Base


class PropertyStep(Base):
    __tablename__ = "tblPropertySteps"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    property_id = Column(Integer, ForeignKey("tblProperties.property_id"), nullable=False, index=True)
    step_id = Column(Integer, ForeignKey("catCatalogSteps.step_id"), nullable=False, index=True)
    completed = Column(Boolean, nullable=False, default=False)
    comments = Column(String, nullable=True)
    completed_at = Column(DateTime(timezone=True), nullable=True)
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    user = relationship("User")
    property = relationship("Property")
    step = relationship("CatalogStep", back_populates="property_steps")

    __table_args__ = (
        UniqueConstraint("user_id", "property_id", "step_id", name="uq_user_property_step"),
    )

    def __repr__(self) -> str:
        return f"<PropertyStep {self.id} user={self.user_id} property={self.property_id} step={self.step_id}>"
