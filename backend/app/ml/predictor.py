import json
import logging
from pathlib import Path

import joblib
import numpy as np
import pandas as pd

logger = logging.getLogger(__name__)

_models = {}
_metadata = {}

_ML_DIR = Path(__file__).resolve().parent.parent.parent / "ml"
_SALE_MODEL_PATH = _ML_DIR / "artifacts" / "sale_model.pkl"
_RENT_MODEL_PATH = _ML_DIR / "artifacts" / "rent_model.pkl"
_SALE_METADATA_PATH = _ML_DIR / "artifacts" / "sale_model_metadata.json"
_RENT_METADATA_PATH = _ML_DIR / "artifacts" / "rent_model_metadata.json"

USD_TO_MXN_RATE = 17.50
MXN_TO_USD_RATE = 1 / USD_TO_MXN_RATE

_STATE_ALIASES = {
    "ciudad de mexico": "Distrito Federal",
    "cdmx": "Distrito Federal",
    "veracruz de ignacio de la llave": "Veracruz",
    "coahuila de zaragoza": "Coahuila",
    "michoacan de ocampo": "Michoacán",
    "nuevo leon": "Nuevo León",
    "queretaro": "Querétaro",
    "quintana roo": "Quintana Roo",
    "san luis potosi": "San Luis Potosí",
    "yucatan": "Yucatán",
    "baja california sur": "Baja California Sur",
    "baja california": "Baja California",
    "estado de mexico": "Estado de México",
}


def _load(operation_type: str = "sale"):
    if operation_type not in _models:
        model_path = _SALE_MODEL_PATH if operation_type == "sale" else _RENT_MODEL_PATH
        if model_path.exists():
            _models[operation_type] = joblib.load(model_path)
        else:
            return None, None
    if operation_type not in _metadata:
        metadata_path = _SALE_METADATA_PATH if operation_type == "sale" else _RENT_METADATA_PATH
        if metadata_path.exists():
            with open(metadata_path, "r") as f:
                _metadata[operation_type] = json.load(f)
    return _models.get(operation_type), _metadata.get(operation_type)


def _normalize_state(state: str) -> str:
    if not state:
        return "Unknown"
    lower = state.lower().strip()
    return _STATE_ALIASES.get(lower, state)


def _normalize_property_type(ptype: str) -> str:
    if not ptype:
        return "other"
    mapping = {
        "house": "house",
        "apartment": "apartment",
        "land": "land",
        "office": "office",
        "commercial_premise": "commercial_premise",
        "warehouse": "warehouse",
        "industrial": "industrial",
        "parking": "other",
    }
    return mapping.get(ptype.lower().strip(), "other")


def predict_price(property_obj) -> dict | None:
    operation_name = property_obj.operation_type.operation_name if property_obj.operation_type else "sale"
    operation_type = "sale" if operation_name == "sale" else "rent"

    model, metadata = _load(operation_type)
    if model is None or metadata is None:
        logger.warning("ML model for '%s' not loaded, skipping price estimation", operation_type)
        return None

    state = _normalize_state(property_obj.state_province)
    ptype = _normalize_property_type(property_obj.property_type)

    area_m2 = float(property_obj.built_area_sqm) if property_obj.built_area_sqm else None
    lot_size = float(property_obj.lot_size_sqm) if property_obj.lot_size_sqm else None

    if area_m2 is None and lot_size is None:
        logger.warning("Property %s has no area, skipping estimation", property_obj.property_id)
        return None

    area = area_m2 or lot_size
    lot = lot_size or area_m2

    lat = float(property_obj.latitude) if property_obj.latitude else None
    lon = float(property_obj.longitude) if property_obj.longitude else None
    bedrooms = float(property_obj.bedrooms) if property_obj.bedrooms else None

    input_data = pd.DataFrame([{
        "property_type": ptype,
        "state": state,
        "area_m2": area,
        "lat": lat if lat is not None else np.nan,
        "lon": lon if lon is not None else np.nan,
        "lot_size_m2": lot if lot is not None else np.nan,
        "bedrooms": bedrooms if bedrooms is not None else np.nan,
    }])

    predictions = model.predict(input_data)
    estimated = float(predictions[0])

    if estimated < 0:
        estimated = 0.0

    std_factor = 0.15
    min_price = max(0.0, estimated * (1 - std_factor))
    max_price = estimated * (1 + std_factor)

    currency = (property_obj.currency or "MXN").upper()
    if currency != "USD":
        estimated *= USD_TO_MXN_RATE
        min_price *= USD_TO_MXN_RATE
        max_price *= USD_TO_MXN_RATE

    confidence = min(metadata["metrics"]["r2_score"], 1.0)

    return {
        "estimated_price": round(estimated, 2),
        "min_price": round(min_price, 2),
        "max_price": round(max_price, 2),
        "confidence_score": round(confidence, 4),
        "model_version": metadata["version"],
        "operation_type": operation_type,
    }
