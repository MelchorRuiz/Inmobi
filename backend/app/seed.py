from sqlalchemy.orm import Session

from app.models.operation_type import OperationType


def seed_operation_types(db: Session):
    operations = ["sale", "rent"]
    for name in operations:
        exists = db.query(OperationType).filter(OperationType.operation_name == name).first()
        if not exists:
            db.add(OperationType(operation_name=name))
    db.commit()
