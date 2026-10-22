from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.dependencies import require_roles
from app.models.item import Item
from app.models.user import Role
from app.schemas.item import ItemCreate, ItemResponse

ANY_ROLE = require_roles(*Role)

router = APIRouter(
    prefix="/items",
    tags=["Items"],
    dependencies=[Depends(ANY_ROLE)],
)

@router.post(
    "/",
    response_model=ItemResponse,
    status_code=status.HTTP_201_CREATED,
    dependencies=[Depends(require_roles(Role.ADMINISTRATOR))],
)
def create_item(item_in: ItemCreate, db: Session = Depends(get_db)):
    db_item = Item(
        title=item_in.title,
        description=item_in.description,
        price=item_in.price,
    )
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[ItemResponse])
def read_items(skip: int = 0, limit: int = 20, db: Session = Depends(get_db)):
    items = db.query(Item).offset(skip).limit(limit).all()
    return items

@router.get("/{item_id}", response_model=ItemResponse)
def read_item(item_id: int, db: Session = Depends(get_db)):
    db_item = db.query(Item).filter(Item.id == item_id).first()
    if not db_item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Item with id {item_id} not found"
        )
    return db_item
