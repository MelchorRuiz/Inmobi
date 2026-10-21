from fastapi import FastAPI

from app.routers import items
from app.routers import health


app = FastAPI(title="Inmobi Backend", version="0.1.0")

app.include_router(items.router)
app.include_router(health.router)