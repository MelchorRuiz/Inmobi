from sqlalchemy.orm import Session

from app.config import settings
from app.models.amenity import Amenity
from app.models.catalog_step import CatalogStep
from app.models.operation_type import OperationType
from app.models.user import Role, User
from app.security import hash_password


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
        "Amueblado",
    ]
    for name in amenities:
        exists = db.query(Amenity).filter(Amenity.name == name).first()
        if not exists:
            db.add(Amenity(name=name))
    db.commit()


def seed_catalog_steps(db: Session):
    sale_op = db.query(OperationType).filter(OperationType.operation_name == "sale").first()
    rent_op = db.query(OperationType).filter(OperationType.operation_name == "rent").first()

    if not sale_op or not rent_op:
        return

    sale_steps = [
        {
            "title": "Definir presupuesto real",
            "description": "Usar la calculadora de precio final de la app.",
            "step_order": 1,
        },
        {
            "title": "Obtener pre-aprobación bancaria (si aplica)",
            "description": "Tener la carta de autorización de crédito antes de hacer ofertas formales.",
            "step_order": 2,
        },
        {
            "title": "Visitar y evaluar propiedades",
            "description": "Contactar al propietario mediante la app y hacer un checklist rápido durante la visita (humedad, presión de agua, instalaciones eléctricas, iluminación, entorno).",
            "step_order": 3,
        },
        {
            "title": "Presentar Carta Oferta",
            "description": "Documento formal con el precio ofrecido, forma de pago y vigencia de la oferta.",
            "step_order": 4,
        },
        {
            "title": "Firma de Contrato de Promesa de Compraventa",
            "description": "Se acuerdan penalizaciones en caso de incumplimiento y fechas límite.",
            "step_order": 5,
        },
        {
            "title": "Pago de apartado / enganche inicial",
            "description": "Transferencia rastreable (nunca en efectivo).",
            "step_order": 6,
        },
        {
            "title": "Solicitar documentación al vendedor",
            "description": "Escritura pública antecedente, boleta predial pagada, recibos de agua/luz al corriente, identificación oficial y acta de matrimonio (si aplica).",
            "step_order": 7,
        },
        {
            "title": "Realizar el Avalúo",
            "description": "El perito valuador visita el inmueble.",
            "step_order": 8,
        },
        {
            "title": "Revisión en Notaría",
            "description": "El notario solicita el Certificado de Libertad de Gravamen para asegurar que la casa no tiene hipotecas vigentes o embargos.",
            "step_order": 9,
        },
        {
            "title": "Revisión del proyecto de escritura",
            "description": "Leer el borrador antes del día de la firma para verificar nombres, medidas y colindancias.",
            "step_order": 10,
        },
        {
            "title": "Liquidación de saldos y pago de impuestos",
            "description": "Transferencia del resto del valor del inmueble al vendedor y pago de derechos/honorarios a la notaría.",
            "step_order": 11,
        },
        {
            "title": "Firma de Escritura Pública",
            "description": "Ante Notario Público (comprador, vendedor y representante del banco si hay hipoteca).",
            "step_order": 12,
        },
        {
            "title": "Firma del Acta de Entrega-Recepción",
            "description": "Verificación final de que la casa se entrega en las condiciones pactadas.",
            "step_order": 13,
        },
        {
            "title": "Entrega de llaves",
            "description": "Entrega de llaves del inmueble.",
            "step_order": 14,
        },
        {
            "title": "Cambio de titularidad de servicios",
            "description": "Luz (CFE), agua, gas, predial y administración del condominio.",
            "step_order": 15,
        },
    ]

    rent_steps = [
        {
            "title": "Definir presupuesto real",
            "description": "Usar la calculadora de precio final de la app.",
            "step_order": 1,
        },
        {
            "title": "Visita e inspección de funcionamiento",
            "description": "Contactar y programar una visita mediante el chat de la app y hacer Checklist rápido (probar regaderas/presión de agua, revisar señal de celular/internet, abrir llaves de paso, verificar chapas y ventanas).",
            "step_order": 2,
        },
        {
            "title": "Armar carpeta digital del inquilino",
            "description": "Identificación oficial, comprobantes de ingresos (últimos 3 meses de recibos de nómina o estados de cuenta) y referencias personales/laborales.",
            "step_order": 3,
        },
        {
            "title": "Pago de investigación / Buró de crédito",
            "description": "Alerta anti-fraude en la app: Recordar al usuario nunca depositar dinero para 'apartar' o 'investigar' antes de haber visto el inmueble en persona y verificado la identidad de quien lo muestra.",
            "step_order": 4,
        },
        {
            "title": "Revisión del Contrato de Arrendamiento",
            "description": "Verificar puntos clave (duración forzosa, penalización por salida anticipada, incremento anual de renta, reglas sobre mascotas y reparaciones mayores vs. menores).",
            "step_order": 5,
        },
        {
            "title": "Pago de Póliza Jurídica (si aplica)",
            "description": "Firma con la afianzadora o despacho jurídico.",
            "step_order": 6,
        },
        {
            "title": "Transferencia de Mes de Renta + Depósito",
            "description": "Hacerlo únicamente a la cuenta bancaria del propietario que aparece en el contrato (nunca a terceros sin carta poder notariada).",
            "step_order": 7,
        },
        {
            "title": "Firmar el Inventario y Anexo Fotográfico",
            "description": "Generar un documento con fecha y hora para adjuntarlo al contrato con fotografías del día que recibes las llaves. Así protege su depósito para cuando se vaya.",
            "step_order": 8,
        },
        {
            "title": "Lectura inicial de medidores",
            "description": "Tomar foto a los medidores de luz, agua y gas para no pagar consumos del inquilino anterior.",
            "step_order": 9,
        },
        {
            "title": "Entrega de llaves, controles y tags de acceso",
            "description": "Entrega de llaves, controles y tags de acceso al inmueble.",
            "step_order": 10,
        },
    ]

    for step_data in sale_steps:
        exists = (
            db.query(CatalogStep)
            .filter(
                CatalogStep.operation_type_id == sale_op.operation_type_id,
                CatalogStep.step_order == step_data["step_order"],
            )
            .first()
        )
        if not exists:
            db.add(
                CatalogStep(
                    operation_type_id=sale_op.operation_type_id,
                    title=step_data["title"],
                    description=step_data["description"],
                    step_order=step_data["step_order"],
                )
            )

    for step_data in rent_steps:
        exists = (
            db.query(CatalogStep)
            .filter(
                CatalogStep.operation_type_id == rent_op.operation_type_id,
                CatalogStep.step_order == step_data["step_order"],
            )
            .first()
        )
        if not exists:
            db.add(
                CatalogStep(
                    operation_type_id=rent_op.operation_type_id,
                    title=step_data["title"],
                    description=step_data["description"],
                    step_order=step_data["step_order"],
                )
            )

    db.commit()


def seed_admin(db: Session) -> None:
    email = settings.ADMIN_EMAIL.strip().lower()
    password = settings.ADMIN_PASSWORD

    if not email or not password:
        return

    existing = db.query(User).filter(User.email == email).first()

    if existing:
        existing.password_hash = hash_password(password)
        existing.role = Role.ADMINISTRATOR
        existing.email_verified = True
        db.commit()
        return

    db.add(
        User(
            email=email,
            password_hash=hash_password(password),
            role=Role.ADMINISTRATOR,
            email_verified=True,
            is_active=True,
        )
    )
    db.commit()
