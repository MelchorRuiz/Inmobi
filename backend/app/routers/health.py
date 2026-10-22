from datetime import datetime, timezone

from fastapi import APIRouter, HTTPException, status
from sqlalchemy import text

from app.database import engine
from app.schemas.health import HealthResponse

router = APIRouter(prefix="/health", tags=["health"])

@router.get("")
async def health() -> HealthResponse:
    return HealthResponse(
        status="ok",
        timestamp=datetime.now(timezone.utc),
    )