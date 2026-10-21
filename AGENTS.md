# AGENTS.md

## Repo layout (monorepo)

Three independent apps, each with its own toolchain and **no shared root config** (no top-level `package.json`, `pyproject.toml`, `Makefile`, or CI workflow):

| Dir | Stack | Commands run from |
|---|---|---|
| `backend/` | Python 3.13 · FastAPI · SQLAlchemy 2 · Alembic · SQLite | `backend/` |
| `pwa/` | React 19 · Vite 8 · ESLint 10 flat config | `pwa/` |
| `mobile/` | Kotlin · Jetpack Compose · Gradle 9 / AGP 9.2 | `mobile/` |

Any lint/test/typecheck step must be run inside the app's own directory. There is no root task runner.

## `backend/`

### Setup

- A working virtualenv already exists at `backend/.venv` — `source backend/.venv/bin/activate` before running anything. There is no `pyproject.toml`/`setup.py`; deps come from `requirements.txt`.
- `requirements.txt` is a flat `pip freeze` pin list and is currently identical to the venv's `pip freeze` output. After changing the venv, regenerate it with `pip freeze > requirements.txt` from `backend/`.
- **No test runner is installed** — `pytest` is absent, so `pytest` commands fail. `httpx` is present, so `fastapi.testclient.TestClient` works for ad-hoc endpoint checks (it warns that `httpx2` supersedes `httpx`; `httpx2` is now installed).
- Config lives in `backend/.env` (gitignored, copy from `.env.example`) and is loaded by `app/config.py` via `pydantic-settings`. Only two keys exist today: `DATABASE_URL` (defaults to `sqlite:///./inmobi.db`) and `DEBUG`.
- `app/config.py` uses `env_file=".env"` — a **relative** path, so alembic/uvicorn must be invoked with CWD = `backend/` or the settings load fails.

### Canonical layering

```
app/main.py            FastAPI(title="Inmobi Backend"); includes routers
app/config.py          Settings (BaseSettings) + cached settings singleton
app/database.py        engine, SessionLocal, get_db() generator, Base
app/models/<x>.py      SQLAlchemy ORM model
app/schemas/<x>.py     Pydantic v2 (Base/Create/Response + from_attributes)
app/routers/<x>.py     APIRouter(prefix=..., tags=...) wired in main.py
```

- The data layer is **sync** SQLAlchemy with a session-per-request `Depends(get_db)`.
- Schema is owned by **Alembic**, never `Base.metadata.create_all()`. After changing a model: generate a migration, then `alembic upgrade head`.
- Router paths use a **trailing slash** (`@router.get("/")` → `POST /items/`). Follow the existing style when adding routes.

### Alembic gotcha

`alembic/env.py:26` runs `config.set_main_option("sqlalchemy.url", settings.DATABASE_URL)`, which **overwrites** the `sqlalchemy.url` placeholder in `alembic.ini`. Editing `alembic.ini` has no effect; change `.env` instead.

```bash
source .venv/bin/activate
alembic revision --autogenerate -m "description of the change"
alembic upgrade head        # alembic current -> 7fb1d24a0bdf (head)
alembic downgrade -1
```

Autogenerate only sees models that are imported. `env.py:23` does `import app.models`, so **every new model must be re-exported from `app/models/__init__.py`** or autogenerate will silently skip it.

### Run

```bash
fastapi dev     # http://127.0.0.1:8000, docs at /docs
```

## `pwa/`

- Package manager is **pnpm** (`pnpm-lock.yaml`), but `package-lock.json` is also committed. Use pnpm; leave the npm lockfile alone unless cleaning up.
- Scripts: `pnpm dev`, `pnpm build`, `pnpm lint` (ESLint 10 flat config in `eslint.config.js`, `globalIgnores(['dist'])` only), `pnpm preview`. No typecheck script.
- **No router or state library** — `pnpm-lock.yaml`/deps are `react` + `react-dom` only. `App.jsx` renders `Inicio`, which renders `NavBar`. All links in `NavBar` are plain `<a href>` pointing at routes that don't exist yet (`/vender`, `/arrendar`, …) and will 404 under Vite dev. Don't assume react-router is available; install it before using it.
- **`pnpm lint` currently fails** on a pre-existing error: `src/App.jsx:1 'useState' is defined but never used`. Don't assume a lint failure is your regression — check `git stash` first or fix that line.
- Plain `.jsx` with component-scoped `.css` files imported directly (`import './App.css'`); no CSS modules, no Tailwind, no `tsconfig`.

## `mobile/`

- Gradle 9 / AGP 9.2.1 / Kotlin 2.2.10 with the version catalog at `gradle/libs.versions.toml` — **add dependencies there**, not inline in `app/build.gradle.kts` (the existing `material-icons-extended` line is the one exception).
- `compileSdk` uses the new block DSL (`compileSdk { version = release(37) { minorApiLevel = 1 } }`), `targetSdk = 35`, `minSdk = 24`.
- **Navigation is hand-rolled**: `MainActivity.kt` holds `var currentScreen by remember { mutableStateOf("home") }` and dispatches with a `when` block over `"home" | "login" | "register"`. No navigation library — follow that pattern unless asked otherwise.
- **Screens live in the `ui/theme/` package** (`HomeScreen.kt`, `LoginScreen.kt`, `RegisterScreen.kt` are `ui.theme.*`, not `ui.screens.*`). Odd, but that's the existing convention.
- `./gradlew assembleDebug` to build; instrumented tests (`app/src/androidTest/.../ExampleInstrumentedTest.kt`) need an emulator/device — no unit tests are configured.
- `local.properties` (SDK path) and `.idea/` **are partially tracked** in git; never commit a regenerated `local.properties`.

## Known traps

- `requirements.txt:1` pins `agent-detector==2.0.0`, which is almost certainly a typo/placeholder. Don't import from it; if you need a dev tool, add it explicitly.
- `pwa/` lint is **red on a clean tree** (`src/App.jsx:1` unused `useState`) — establish the baseline before claiming your change broke something. The backend has **no linter installed at all**: `pyflakes` was dropped from `requirements.txt`, so `python -m pyflakes` fails until you reinstall it.
- `backend/.gitignore` ignores `inmobi.db` and `.env`, and both are correctly untracked. `git ls-files` is the check when unsure.
- Android `applicationId` / `namespace` is still the template value `com.example.inmobi`; it is unrelated to the product name and safe to leave.