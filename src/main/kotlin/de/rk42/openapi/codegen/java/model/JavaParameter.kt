package de.rk42.openapi.codegen.java.model

data class JavaParameter(
    val javaIdentifier: String,
    val location: JavaParameterLocation,
    val description: String?,
    val required: Boolean,
    val javaType: JavaReference
)