from functools import lru_cache
from typing import Literal

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    DEBUG: bool = False
    ENVIRONMENT: Literal["development", "production"] = "development"
    DATABASE_URL: str
    SECRET_KEY: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60
    REFRESH_TOKEN_EXPIRE_DAYS: int = 30
    GOOGLE_CLIENT_ID: str = ""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    @property
    def cors_origins(self) -> list[str]:
        if self.ENVIRONMENT == "development":
            return ["http://localhost:5173", "http://127.0.0.1:5173"]
        return []

    @property
    def refresh_cookie_secure(self) -> bool:
        return self.ENVIRONMENT == "production"

    @property
    def refresh_cookie_samesite(self) -> str:
        return "lax"


@lru_cache
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
