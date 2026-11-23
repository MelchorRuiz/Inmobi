from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, WebSocket, WebSocketDisconnect, status
from sqlalchemy import func, or_
from sqlalchemy.orm import Session

from app.database import get_db
from app.dependencies import get_current_user
from app.models.chat import Chat
from app.models.message import Message
from app.models.property import Property
from app.models.user import User
from app.schemas.chat import (
    ChatCreate,
    ChatDetail,
    ChatSummary,
    MessageCreate,
    MessageResponse,
    OtherUserSummary,
    PropertySummary,
)
from app.websocket import manager

router = APIRouter(prefix="/chats", tags=["Chats"])


@router.post("/", response_model=ChatSummary)
def create_or_get_chat(
    payload: ChatCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    property = db.get(Property, payload.property_id)
    if property is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="property not found",
        )

    recipient_user_id = property.owner_user_id

    if recipient_user_id == current_user.id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="cannot create a chat with yourself",
        )

    user_1_id = min(current_user.id, recipient_user_id)
    user_2_id = max(current_user.id, recipient_user_id)

    chat = (
        db.query(Chat)
        .filter(
            Chat.property_id == payload.property_id,
            Chat.user_1_id == user_1_id,
            Chat.user_2_id == user_2_id,
        )
        .first()
    )

    if chat is None:
        chat = Chat(
            property_id=payload.property_id,
            user_1_id=user_1_id,
            user_2_id=user_2_id,
        )
        db.add(chat)
        db.commit()
        db.refresh(chat)

    other_user = db.get(User, recipient_user_id)
    last_message = db.query(Message).filter(Message.chat_id == chat.id).order_by(Message.created_at.desc()).first()
    unread_count = (
        db.query(func.count(Message.id))
        .filter(
            Message.chat_id == chat.id,
            Message.sender_id != current_user.id,
            Message.read_at.is_(None),
        )
        .scalar()
        or 0
    )

    return ChatSummary(
        chat_id=chat.id,
        other_user=OtherUserSummary.model_validate(other_user),
        property=PropertySummary.model_validate(chat.property),
        last_message=last_message.content if last_message else None,
        unread_count=unread_count,
        last_message_at=last_message.created_at if last_message else None,
    )


@router.get("/", response_model=list[ChatSummary])
def list_chats(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    chats = (
        db.query(Chat)
        .filter(or_(Chat.user_1_id == current_user.id, Chat.user_2_id == current_user.id))
        .all()
    )

    result = []
    for chat in chats:
        other_user = chat.user_2 if chat.user_1_id == current_user.id else chat.user_1

        last_message = (
            db.query(Message)
            .filter(Message.chat_id == chat.id)
            .order_by(Message.created_at.desc())
            .first()
        )

        unread_count = (
            db.query(func.count(Message.id))
            .filter(
                Message.chat_id == chat.id,
                Message.sender_id != current_user.id,
                Message.read_at.is_(None),
            )
            .scalar()
            or 0
        )

        result.append(
            ChatSummary(
                chat_id=chat.id,
                other_user=OtherUserSummary.model_validate(other_user),
                property=PropertySummary.model_validate(chat.property),
                last_message=last_message.content if last_message else None,
                unread_count=unread_count,
                last_message_at=last_message.created_at if last_message else None,
            )
        )

    def _sort_key(x):
        dt = x.last_message_at
        if dt is None:
            return datetime.min.replace(tzinfo=timezone.utc)
        if dt.tzinfo is None:
            return dt.replace(tzinfo=timezone.utc)
        return dt

    result.sort(key=_sort_key, reverse=True)
    return result


@router.get("/{chat_id}/", response_model=ChatDetail)
def get_chat(
    chat_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    chat = db.get(Chat, chat_id)
    if chat is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="chat not found",
        )

    if chat.user_1_id != current_user.id and chat.user_2_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="you do not have access to this chat",
        )

    messages = db.query(Message).filter(Message.chat_id == chat_id).order_by(Message.created_at.asc()).all()

    now = datetime.now(timezone.utc)
    updated_count = (
        db.query(Message)
        .filter(
            Message.chat_id == chat_id,
            Message.sender_id != current_user.id,
            Message.read_at.is_(None),
        )
        .update({Message.read_at: now}, synchronize_session=False)
    )

    if updated_count > 0:
        db.commit()

    other_user = chat.user_2 if chat.user_1_id == current_user.id else chat.user_1

    return ChatDetail(
        chat_id=chat.id,
        other_user=OtherUserSummary.model_validate(other_user),
        property=PropertySummary.model_validate(chat.property),
        messages=[MessageResponse.model_validate(m) for m in messages],
        unread_count=0,
    )


@router.post("/{chat_id}/messages/", response_model=MessageResponse)
async def send_message(
    chat_id: int,
    payload: MessageCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    chat = db.get(Chat, chat_id)
    if chat is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="chat not found",
        )

    if chat.user_1_id != current_user.id and chat.user_2_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="you do not have access to this chat",
        )

    message = Message(
        chat_id=chat_id,
        sender_id=current_user.id,
        content=payload.content,
    )
    db.add(message)
    db.commit()
    db.refresh(message)

    recipient_id = chat.user_2 if chat.user_1_id == current_user.id else chat.user_1
    await manager.send_to_user(
        recipient_id.id,
        {
            "type": "new_message",
            "chat_id": chat_id,
            "sender_id": current_user.id,
            "content": payload.content,
            "created_at": message.created_at.isoformat(),
        },
    )

    return MessageResponse.model_validate(message)


@router.websocket("/ws/")
async def websocket_endpoint(websocket: WebSocket):
    user_id = await manager.authenticate(websocket)
    if user_id is None:
        return

    await manager.connect(user_id, websocket)
    try:
        while True:
            await websocket.receive_text()
    except WebSocketDisconnect:
        manager.disconnect(user_id)
