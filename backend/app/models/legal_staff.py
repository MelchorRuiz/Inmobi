from sqlalchemy import Column, ForeignKey, String
from sqlalchemy.orm import relationship
from app.database import Base


class LegalStaff(Base):
    __tablename__ = "legal_staff"

    user_id = Column(
        ForeignKey("users.id", ondelete="CASCADE"),
        primary_key=True,
        index=True,
        autoincrement=False,
    )
    id_number = Column(String, nullable=False, unique=True, index=True)
    specialty = Column(String, nullable=False)
    firm_name = Column(String, nullable=True)
    phone = Column(String, nullable=True)

    user = relationship("User", back_populates="legal_staff")

    def __repr__(self) -> str:
        return f"<LegalStaff {self.user_id} {self.id_number}>"
