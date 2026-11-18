from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.seed import seed_operation_types
from app.database import SessionLocal
from app.routers import auth
from app.routers import end_users
from app.routers import health
from app.routers import properties


@asynccontextmanager
async def lifespan(app: FastAPI):
    db = SessionLocal()
    try:
        seed_operation_types(db)
    finally:
        db.close()
    yield


app = FastAPI(title="Inmobi Backend", version="0.1.0", lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(end_users.router)
app.include_router(properties.router)
app.include_router(health.router)
