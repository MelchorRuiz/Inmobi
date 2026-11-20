from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent
DATA_DIR = BASE_DIR / "data"
RAW_DATA_DIR = DATA_DIR / "raw"
PROCESSED_DATA_DIR = DATA_DIR / "processed"
ARTIFACTS_DIR = BASE_DIR / "artifacts"

DATASET_PATH = RAW_DATA_DIR / "mexico-real-state-clean.csv"
MODEL_PATH = ARTIFACTS_DIR / "property_price_model.pkl"
PREPROCESSOR_PATH = ARTIFACTS_DIR / "preprocessor.pkl"
METADATA_PATH = ARTIFACTS_DIR / "model_metadata.json"

MODEL_VERSION = "2.1.0"

USD_TO_MXN_RATE = 17.50

CORE_FEATURES = [
    "property_type",
    "state",
    "area_m2",
]

OPTIONAL_FEATURES = [
    "lat",
    "lon",
    "lot_size_m2",
    "bedrooms",
]

ALL_FEATURES = CORE_FEATURES + OPTIONAL_FEATURES

TARGET_COLUMN = "price_usd"

SALE_MODEL_PATH = ARTIFACTS_DIR / "sale_model.pkl"
RENT_MODEL_PATH = ARTIFACTS_DIR / "rent_model.pkl"
SALE_METADATA_PATH = ARTIFACTS_DIR / "sale_model_metadata.json"
RENT_METADATA_PATH = ARTIFACTS_DIR / "rent_model_metadata.json"

# Remove old paths
MODEL_PATH = SALE_MODEL_PATH
METADATA_PATH = SALE_METADATA_PATH
