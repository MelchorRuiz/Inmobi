from pydantic import BaseModel, ConfigDict


class OperationTypeResponse(BaseModel):
    operation_type_id: int
    operation_name: str

    model_config = ConfigDict(from_attributes=True)
