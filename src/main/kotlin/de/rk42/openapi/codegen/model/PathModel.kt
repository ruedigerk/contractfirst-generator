package de.rk42.openapi.codegen.model

data class PathModel(
    val path: String,
    val summary: String,
    val description: String,
    val operations: List<OperationModel>
)
