from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from app.database import Base


class OperationType(Base):
    __tablename__ = "catOperationTypes"

    operation_type_id = Column(Integer, primary_key=True, index=True)
    operation_name = Column(String, nullable=False, unique=True)

    properties = relationship("Property", back_populates="operation_type")
