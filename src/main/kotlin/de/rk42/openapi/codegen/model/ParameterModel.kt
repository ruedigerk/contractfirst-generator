package de.rk42.openapi.codegen.model

data class ParameterModel(
    val name: String,
    val location: String,
    val description: String,
    val required: Boolean
)