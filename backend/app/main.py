from fastapi import FastAPI

from app.routers import auth
from app.routers import end_users
from app.routers import health
from app.routers import items


app = FastAPI(title="Inmobi Backend", version="0.1.0")

app.include_router(auth.router)
app.include_router(end_users.router)
app.include_router(items.router)
app.include_router(health.router)
