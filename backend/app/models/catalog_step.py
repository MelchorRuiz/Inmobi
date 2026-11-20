from sqlalchemy import Column, DateTime, ForeignKey, Integer, String, func
from sqlalchemy.orm import relationship
from app.database import Base


class CatalogStep(Base):
    __tablename__ = "catCatalogSteps"

    step_id = Column(Integer, primary_key=True, index=True)
    operation_type_id = Column(
        Integer, ForeignKey("catOperationTypes.operation_type_id"), nullable=False, index=True
    )
    title = Column(String, nullable=False)
    description = Column(String, nullable=False)
    step_order = Column(Integer, nullable=False)
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    operation_type = relationship("OperationType")
    property_steps = relationship("PropertyStep", back_populates="step", cascade="all, delete-orphan")

    def __repr__(self) -> str:
        return f"<CatalogStep {self.step_id} {self.title}>"
