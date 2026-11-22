from contextlib import asynccontextmanager
from pathlib import Path

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.config import settings
from app.seed import seed_amenities, seed_catalog_steps, seed_operation_types
from app.database import SessionLocal
from app.routers import amenities, auth, dashboard, end_users, files, health, journey, properties


FILES_DIR = Path(__file__).resolve().parent.parent / "files"


@asynccontextmanager
async def lifespan(app: FastAPI):
    db = SessionLocal()
    try:
        seed_operation_types(db)
        seed_amenities(db)
        seed_catalog_steps(db)
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
app.include_router(amenities.router)
app.include_router(journey.router)
app.include_router(dashboard.router)
app.include_router(files.router)
app.include_router(health.router)

FILES_DIR.mkdir(parents=True, exist_ok=True)
app.mount("/files", StaticFiles(directory=str(FILES_DIR)), name="files")
