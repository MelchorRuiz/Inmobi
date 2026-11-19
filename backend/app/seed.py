from sqlalchemy.orm import Session

from app.models.amenity import Amenity
from app.models.operation_type import OperationType


def seed_operation_types(db: Session):
    operations = ["sale", "rent"]
    for name in operations:
        exists = db.query(OperationType).filter(OperationType.operation_name == name).first()
        if not exists:
            db.add(OperationType(operation_name=name))
    db.commit()


def seed_amenities(db: Session):
    amenities = [
        "Alberca",
        "Gimnasio",
        "Seguridad 24h",
        "Jardín",
        "Estacionamiento de visitas",
        "Elevador",
        "Salón de eventos",
        "Área de lavandería",
        "Terraza",
        "Cuarto de servicio",
        "Cancha deportiva",
        "Área infantil",
        "Área de asador",
        "Pet friendly",
        "Amoblado",
    ]
    for name in amenities:
        exists = db.query(Amenity).filter(Amenity.name == name).first()
        if not exists:
            db.add(Amenity(name=name))
    db.commit()
