package de.rk42.openapi.codegen.model

data class OperationModel(
    val method: String,
    val tags: List<String>,
    val summary: String,
    val description: String,
    val operationId: String,
    val parameters: List<ParameterModel>
)
