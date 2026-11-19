from enum import Enum

from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.orm import relationship
from app.database import Base


class MediaType(str, Enum):
    IMAGE = "image"
    VIDEO = "video"


class PropertyMedia(Base):
    __tablename__ = "tblPropertyMedia"

    media_id = Column(Integer, primary_key=True, index=True)
    property_id = Column(
        Integer, ForeignKey("tblProperties.property_id", ondelete="CASCADE"), nullable=False, index=True
    )
    media_type = Column(String, nullable=False)
    file_url = Column(String, nullable=False)
    display_order = Column(Integer, nullable=False, default=0)

    property = relationship("Property", back_populates="media")
