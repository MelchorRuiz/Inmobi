import os
import uuid
from pathlib import Path

from fastapi import APIRouter, Depends, HTTPException, UploadFile, status

from app.dependencies import get_current_user
from app.models.user import User

router = APIRouter(prefix="/files", tags=["Files"])

FILES_DIR = Path(__file__).resolve().parent.parent.parent / "files"

ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".mp4", ".mov", ".avi"}


@router.post("/upload/")
async def upload_file(
    file: UploadFile,
    current_user: User = Depends(get_current_user),
):
    if file.filename:
        ext = Path(file.filename).suffix.lower()
    else:
        ext = ""

    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"File type not allowed. Allowed types: {', '.join(ALLOWED_EXTENSIONS)}",
        )

    FILES_DIR.mkdir(parents=True, exist_ok=True)

    filename = f"{uuid.uuid4()}{ext}"
    filepath = FILES_DIR / filename

    content = await file.read()
    with open(filepath, "wb") as f:
        f.write(content)

    return {"url": f"files/{filename}"}
