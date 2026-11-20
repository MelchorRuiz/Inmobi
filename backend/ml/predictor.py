import json
import sys
from pathlib import Path

import joblib
import numpy as np
import pandas as pd

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from ml.config import (
    SALE_MODEL_PATH,
    RENT_MODEL_PATH,
    SALE_METADATA_PATH,
    RENT_METADATA_PATH,
    ALL_FEATURES,
    USD_TO_MXN_RATE,
)


_models = {}
_metadata = {}


def _load(operation_type: str = "sale"):
    if operation_type not in _models:
        model_path = SALE_MODEL_PATH if operation_type == "sale" else RENT_MODEL_PATH
        _models[operation_type] = joblib.load(model_path)
    if operation_type not in _metadata:
        metadata_path = SALE_METADATA_PATH if operation_type == "sale" else RENT_METADATA_PATH
        with open(metadata_path, "r") as f:
            _metadata[operation_type] = json.load(f)
    return _models[operation_type], _metadata[operation_type]


def predict_price(
    property_type: str,
    state: str,
    area_m2: float,
    lat: float = None,
    lon: float = None,
    lot_size_m2: float = None,
    bedrooms: float = None,
    operation_type: str = "sale",
    currency: str = "USD",
) -> dict:
    model, metadata = _load(operation_type)

    input_data = pd.DataFrame([{
        "property_type": property_type,
        "state": state,
        "area_m2": area_m2,
        "lat": lat if lat is not None else np.nan,
        "lon": lon if lon is not None else np.nan,
        "lot_size_m2": lot_size_m2 if lot_size_m2 is not None else np.nan,
        "bedrooms": bedrooms if bedrooms is not None else np.nan,
    }])

    predictions = model.predict(input_data)
    estimated = float(predictions[0])

    if estimated < 0:
        estimated = 0

    std_factor = 0.15
    min_price = max(0, estimated * (1 - std_factor))
    max_price = estimated * (1 + std_factor)

    if currency.upper() == "MXN":
        estimated *= USD_TO_MXN_RATE
        min_price *= USD_TO_MXN_RATE
        max_price *= USD_TO_MXN_RATE

    confidence = min(metadata["metrics"]["r2_score"], 1.0)

    return {
        "estimated_price": round(estimated, 2),
        "min_price": round(min_price, 2),
        "max_price": round(max_price, 2),
        "confidence_score": round(confidence, 4),
        "currency": currency.upper(),
        "model_version": metadata["version"],
    }


if __name__ == "__main__":
    print("=== SALE Predictions ===\n")
    
    sale_tests = [
        {
            "property_type": "house",
            "state": "Distrito Federal",
            "area_m2": 150,
            "lat": 19.43,
            "lon": -99.13,
            "lot_size_m2": 200,
            "bedrooms": 3,
            "operation_type": "sale",
            "currency": "MXN",
        },
        {
            "property_type": "apartment",
            "state": "Nuevo León",
            "area_m2": 100,
            "lat": 25.68,
            "lon": -100.20,
            "lot_size_m2": 100,
            "bedrooms": 2,
            "operation_type": "sale",
            "currency": "MXN",
        },
    ]

    for i, tc in enumerate(sale_tests, 1):
        result = predict_price(**tc)
        print(f"Test {i}: {tc['property_type']} in {tc['state']} ({tc['area_m2']}m²)")
        print(f"  Estimated: {result['currency']} {result['estimated_price']:,.2f}")
        print(f"  Range:     {result['currency']} {result['min_price']:,.2f} - {result['max_price']:,.2f}")
        print(f"  Confidence: {result['confidence_score']:.2%}")
        print(f"  Model:     v{result['model_version']}")
        print()

    print("=== RENT Predictions ===\n")

    rent_tests = [
        {
            "property_type": "apartment",
            "state": "Distrito Federal",
            "area_m2": 80,
            "lat": 19.43,
            "lon": -99.13,
            "lot_size_m2": 80,
            "bedrooms": 2,
            "operation_type": "rent",
            "currency": "MXN",
        },
        {
            "property_type": "house",
            "state": "Yucatán",
            "area_m2": 150,
            "lot_size_m2": 200,
            "bedrooms": 3,
            "operation_type": "rent",
            "currency": "MXN",
        },
        {
            "property_type": "office",
            "state": "Nuevo León",
            "area_m2": 60,
            "lot_size_m2": 60,
            "bedrooms": None,
            "operation_type": "rent",
            "currency": "MXN",
        },
    ]

    for i, tc in enumerate(rent_tests, 1):
        result = predict_price(**tc)
        print(f"Test {i}: {tc['property_type']} in {tc['state']} ({tc['area_m2']}m²)")
        print(f"  Estimated: {result['currency']} {result['estimated_price']:,.2f}/month")
        print(f"  Range:     {result['currency']} {result['min_price']:,.2f} - {result['max_price']:,.2f}")
        print(f"  Confidence: {result['confidence_score']:.2%}")
        print(f"  Model:     v{result['model_version']}")
        print()
