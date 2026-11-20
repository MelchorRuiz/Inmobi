import json
import sys
import re
from pathlib import Path

import joblib
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, r2_score, mean_absolute_error

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from ml.config import (
    DATASET_PATH,
    SALE_MODEL_PATH,
    RENT_MODEL_PATH,
    SALE_METADATA_PATH,
    RENT_METADATA_PATH,
    ARTIFACTS_DIR,
    MODEL_VERSION,
    CORE_FEATURES,
    ALL_FEATURES,
    TARGET_COLUMN,
)
from ml.model import create_model


INMUEBLES24_PATH = Path(__file__).resolve().parent / "data" / "raw" / "inmuebles24-mexico.csv"
MEXICO_CITY_PATHS = [
    Path(__file__).resolve().parent / "data" / "raw" / f"mexico-city-real-estate-{i}.csv"
    for i in range(1, 6)
]


def extract_numeric(value):
    if pd.isna(value):
        return np.nan
    if isinstance(value, (int, float)):
        return float(value)
    match = re.search(r'[\d,]+\.?\d*', str(value).replace(',', ''))
    if match:
        return float(match.group().replace(',', ''))
    return np.nan


def load_mexico_city_data() -> pd.DataFrame:
    dfs = []
    for path in MEXICO_CITY_PATHS:
        if path.exists():
            df = pd.read_csv(path, encoding='utf-8-sig')
            dfs.append(df)
    
    if not dfs:
        return pd.DataFrame()
    
    combined = pd.concat(dfs, ignore_index=True)
    print(f"[INFO] Loaded {len(combined)} rows from mexico-city-real-estate files")
    
    combined = combined[combined['operation'] == 'sell'].copy()
    print(f"[INFO] After filtering 'sell': {len(combined)} rows")
    
    combined['property_type'] = combined['property_type'].str.lower().str.strip()
    type_mapping = {
        'house': 'house',
        'apartment': 'apartment',
        'land': 'land',
        'commercial': 'commercial_premise',
        'office': 'office',
        'industrial': 'industrial',
        'parking': 'parking',
    }
    combined['property_type'] = combined['property_type'].map(type_mapping).fillna('other')
    
    combined['state'] = combined['place_with_parent_names'].str.extract(r'\|([^|]+)\|')
    combined['state'] = combined['state'].fillna('Unknown')
    
    latlon = combined['lat-lon'].str.split(',', expand=True)
    combined['lat'] = pd.to_numeric(latlon[0], errors='coerce')
    combined['lon'] = pd.to_numeric(latlon[1], errors='coerce')
    
    combined['price_usd'] = pd.to_numeric(combined['price_aprox_usd'], errors='coerce')
    combined['built_area_m2'] = pd.to_numeric(combined['surface_covered_in_m2'], errors='coerce')
    combined['lot_size_m2'] = pd.to_numeric(combined['surface_total_in_m2'], errors='coerce')
    combined['area_m2'] = combined['built_area_m2'].fillna(combined['lot_size_m2'])
    combined['bedrooms'] = pd.to_numeric(combined['rooms'], errors='coerce')
    combined['bathrooms'] = np.nan
    combined['parking_spaces'] = np.nan
    combined['property_age_years'] = np.nan
    combined['currency'] = combined['currency'].fillna('USD')
    combined['source'] = 'kaggle_mexico_city'
    
    return combined


def load_and_prepare_data() -> dict:
    print(f"[INFO] Loading dataset from {DATASET_PATH}")
    df1 = pd.read_csv(DATASET_PATH)
    print(f"[INFO] Loaded {len(df1)} rows from mexico-real-state-clean.csv")
    
    df1['property_type'] = df1['property_type'].str.lower().str.strip()
    df1['state'] = df1['state'].str.strip()
    df1 = df1.rename(columns={
        'area_m2': 'built_area_m2',
    })
    df1['lot_size_m2'] = df1['built_area_m2']
    df1['bedrooms'] = np.nan
    df1['bathrooms'] = np.nan
    df1['parking_spaces'] = np.nan
    df1['property_age_years'] = np.nan
    df1['operation_type'] = 'sale'
    df1['currency'] = 'USD'
    df1['source'] = 'github'
    
    print(f"[INFO] Loading dataset from {INMUEBLES24_PATH}")
    df2 = pd.read_csv(INMUEBLES24_PATH)
    print(f"[INFO] Loaded {len(df2)} rows from inmuebles24-mexico.csv")
    
    df2['property_type'] = df2['Tipo_de_inmueble'].str.lower().str.strip()
    type_mapping = {
        'casa': 'house',
        'departamento': 'apartment',
        'casa en condominio': 'house',
        'dúplex': 'house',
        'villa': 'house',
        'quinta': 'house',
        'terreno / lote': 'land',
        'terreno comercial': 'land',
        'terreno industrial': 'land',
        'oficina': 'office',
        'local comercial': 'commercial_premise',
        'local en centro comercial': 'commercial_premise',
        'nave industrial': 'industrial',
        'bodega comercial': 'warehouse',
        'bodega-galpón': 'warehouse',
        'edificio': 'commercial_premise',
        'casa uso de suelo': 'house',
        'rancho': 'land',
        'desarrollo vertical': 'apartment',
    }
    df2['property_type'] = df2['property_type'].map(type_mapping).fillna('other')
    
    df2['state'] = df2['Provincia'].str.strip()
    df2['built_area_m2'] = df2['Dimension_propiedad'].apply(extract_numeric)
    df2['lot_size_m2'] = df2['Dimension_terreno'].apply(extract_numeric)
    df2['bedrooms'] = pd.to_numeric(df2['Dormitorios'], errors='coerce')
    df2['bathrooms'] = pd.to_numeric(df2['Banos'], errors='coerce')
    df2['parking_spaces'] = pd.to_numeric(df2['Estacionamiento'], errors='coerce')
    df2['property_age_years'] = df2['Antiguedad'].apply(extract_numeric)
    df2['operation_type'] = df2['Operacion'].apply(lambda x: 'sale' if x == 'venta' else 'rent')
    df2['currency'] = df2['currency'].fillna('MXN')
    df2['lat'] = df2['latitude']
    df2['lon'] = df2['longitude']
    df2['price_usd'] = df2.apply(
        lambda row: row['Precio'] / 17.5 if row['currency'] == 'MXN' else row['Precio'],
        axis=1
    )
    df2['area_m2'] = df2['built_area_m2'].fillna(df2['lot_size_m2'])
    df2['source'] = 'inmuebles24'
    
    print(f"[INFO] Inmuebles24 - operation_type distribution:\n{df2['operation_type'].value_counts()}")
    print(f"[INFO] Inmuebles24 - property_type distribution:\n{df2['property_type'].value_counts()}")
    
    df3 = load_mexico_city_data()
    print(f"[INFO] Mexico City Kaggle - property_type distribution:\n{df3['property_type'].value_counts()}")
    
    combined = pd.concat([df1, df2, df3], ignore_index=True)
    print(f"[INFO] Combined dataset: {len(combined)} rows")
    
    required_cols = ['property_type', 'state', 'price_usd']
    combined = combined.dropna(subset=required_cols)
    print(f"[INFO] After dropping NaNs in required cols: {len(combined)} rows")
    
    combined['area_m2'] = combined.apply(
        lambda row: row['area_m2'] if pd.notna(row.get('area_m2')) and row['area_m2'] > 0 
        else (row['built_area_m2'] if pd.notna(row.get('built_area_m2')) and row.get('built_area_m2', 0) > 0 
        else row.get('lot_size_m2', 0)), axis=1
    )
    
    combined = combined[combined['area_m2'] > 0]
    combined = combined[combined['price_usd'] > 0]
    
    # Separate by operation_type before filtering outliers
    # Filter out rows without operation_type
    combined = combined.dropna(subset=['operation_type'])
    datasets = {}
    for op_type in combined['operation_type'].unique():
        op_data = combined[combined['operation_type'] == op_type].copy()
        print(f"\n[INFO] === Processing {str(op_type).upper()} data ===")
        print(f"[INFO] Before filtering outliers: {len(op_data)} rows")
        
        # Filter outliers per operation type
        op_data = op_data[op_data['price_usd'] < op_data['price_usd'].quantile(0.99)]
        print(f"[INFO] After filtering outliers: {len(op_data)} rows")
        
        datasets[str(op_type)] = op_data
    
    print(f"\n[INFO] Final datasets:")
    for op_type, data in datasets.items():
        print(f"  {op_type}: {len(data)} rows")
        print(f"    Property types: {data['property_type'].value_counts().to_dict()}")
    
    return datasets


def evaluate_model(model, X_test, y_test) -> dict:
    y_pred = model.predict(X_test)

    mse = mean_squared_error(y_test, y_pred)
    rmse = np.sqrt(mse)
    mae = mean_absolute_error(y_test, y_pred)
    r2 = r2_score(y_test, y_pred)
    mape = np.mean(np.abs((y_test - y_pred) / y_test)) * 100

    return {
        "rmse": round(float(rmse), 2),
        "mae": round(float(mae), 2),
        "r2_score": round(float(r2), 4),
        "mape": round(float(mape), 2),
    }


def train():
    ARTIFACTS_DIR.mkdir(parents=True, exist_ok=True)

    datasets = load_and_prepare_data()

    results = {}

    for operation_type, data in datasets.items():
        print(f"\n{'='*60}")
        print(f"TRAINING MODEL: {operation_type.upper()}")
        print(f"{'='*60}")

        core_features = ['property_type', 'state', 'area_m2']
        optional_features = ['lat', 'lon', 'lot_size_m2', 'bedrooms']
        all_features = core_features + optional_features
        
        X = data[all_features]
        y = data[TARGET_COLUMN]

        print(f"[INFO] Core Features: {core_features}")
        print(f"[INFO] Optional Features: {optional_features}")
        print(f"[INFO] Target: {TARGET_COLUMN}")
        print(f"[INFO] X shape: {X.shape}")
        print(f"[INFO] Non-null counts:")
        for col in all_features:
            non_null = X[col].notna().sum()
            print(f"  {col}: {non_null}/{len(X)} ({non_null/len(X)*100:.1f}%)")

        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )
        print(f"[INFO] Train size: {len(X_train)}, Test size: {len(X_test)}")

        print(f"[INFO] Creating and training {operation_type} model...")
        model = create_model(operation_type)
        model.fit(X_train, y_train)
        print("[INFO] Training complete")

        print("[INFO] Evaluating model...")
        metrics = evaluate_model(model, X_test, y_test)
        print(f"[INFO] Model metrics: {metrics}")

        # Select correct path based on operation type
        if operation_type == "sale":
            model_path = SALE_MODEL_PATH
            metadata_path = SALE_METADATA_PATH
        else:
            model_path = RENT_MODEL_PATH
            metadata_path = RENT_METADATA_PATH

        print(f"[INFO] Saving model to {model_path}")
        joblib.dump(model, model_path)

        metadata = {
            "version": MODEL_VERSION,
            "operation_type": operation_type,
            "core_features": core_features,
            "optional_features": optional_features,
            "all_features": all_features,
            "target": TARGET_COLUMN,
            "metrics": metrics,
            "train_size": len(X_train),
            "test_size": len(X_test),
            "datasets": ["mexico-real-state-clean.csv", "inmuebles24-mexico.csv", "mexico-city-real-estate-1-5.csv"],
        }

        print(f"[INFO] Saving metadata to {metadata_path}")
        with open(metadata_path, "w") as f:
            json.dump(metadata, f, indent=2)

        results[operation_type] = {
            "model_path": model_path,
            "metadata_path": metadata_path,
            "metrics": metrics,
        }

    print("\n" + "="*60)
    print("TRAINING SUMMARY")
    print("="*60)
    for op_type, result in results.items():
        print(f"\n{op_type.upper()} Model:")
        print(f"  Path: {result['model_path']}")
        print(f"  Metrics: {result['metrics']}")

    print("[INFO] Done!")
    return results


if __name__ == "__main__":
    train()
