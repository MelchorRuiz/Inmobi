import numpy as np
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.base import BaseEstimator
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.impute import SimpleImputer

from ml.config import ALL_FEATURES


def create_preprocessor() -> ColumnTransformer:
    categorical_features = ["property_type", "state"]
    numerical_features = ["area_m2", "lat", "lon", "lot_size_m2", "bedrooms"]

    categorical_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="constant", fill_value="unknown")),
        ("onehot", OneHotEncoder(handle_unknown="ignore", sparse_output=False)),
    ])

    numerical_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="median", fill_value=0)),
        ("scaler", StandardScaler()),
    ])

    preprocessor = ColumnTransformer(
        transformers=[
            ("cat", categorical_transformer, categorical_features),
            ("num", numerical_transformer, numerical_features),
        ]
    )

    return preprocessor


def create_model(operation_type: str = "sale") -> Pipeline:
    preprocessor = create_preprocessor()

    if operation_type == "sale":
        regressor = GradientBoostingRegressor(
            n_estimators=500,
            learning_rate=0.05,
            max_depth=8,
            min_samples_split=20,
            min_samples_leaf=10,
            subsample=0.8,
            max_features="sqrt",
            random_state=42,
        )
    else:
        # Rent model: simpler to avoid overfitting with ~241 samples
        regressor = GradientBoostingRegressor(
            n_estimators=100,
            learning_rate=0.1,
            max_depth=4,
            min_samples_split=10,
            min_samples_leaf=5,
            subsample=0.9,
            max_features="sqrt",
            random_state=42,
        )

    model = Pipeline(steps=[
        ("preprocessor", preprocessor),
        ("regressor", regressor),
    ])

    return model


def predict_with_intervals(model: Pipeline, X: np.ndarray) -> dict:
    point_predictions = model.predict(X)

    residuals = []
    if hasattr(model, "named_steps") and "regressor" in model.named_steps:
        regressor = model.named_steps["regressor"]
        if hasattr(regressor, "estimators_"):
            all_predictions = np.array([
                tree[0].predict(model.named_steps["preprocessor"].transform(X))
                for tree in regressor.estimators_
            ])
            lower = np.percentile(all_predictions, 10, axis=0)
            upper = np.percentile(all_predictions, 90, axis=0)
            return {
                "estimated": point_predictions,
                "min": lower,
                "max": upper,
            }

    std_dev = np.std(point_predictions) * 0.15
    lower = point_predictions * 0.85
    upper = point_predictions * 1.15

    return {
        "estimated": point_predictions,
        "min": lower,
        "max": upper,
    }
