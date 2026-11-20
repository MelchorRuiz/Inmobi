from sqlalchemy import Column, DateTime, Float, ForeignKey, Integer, Numeric, String, func
from sqlalchemy.orm import relationship
from app.database import Base


class PropertyPriceEstimation(Base):
    __tablename__ = "property_price_estimations"

    estimation_id = Column(Integer, primary_key=True, index=True)
    property_id = Column(
        Integer,
        ForeignKey("tblProperties.property_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        index=True,
    )
    estimated_price = Column(Numeric(12, 2), nullable=False)
    min_price = Column(Numeric(12, 2), nullable=False)
    max_price = Column(Numeric(12, 2), nullable=False)
    confidence_score = Column(Float, nullable=False)
    model_version = Column(String(50), nullable=False)
    operation_type = Column(String(10), nullable=False, default="sale")
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    property = relationship("Property", back_populates="price_estimation")
