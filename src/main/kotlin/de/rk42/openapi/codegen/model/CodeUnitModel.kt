package de.rk42.openapi.codegen.model

data class CodeUnitModel(
    val name: String,
    val operations: List<OperationModel>
)
