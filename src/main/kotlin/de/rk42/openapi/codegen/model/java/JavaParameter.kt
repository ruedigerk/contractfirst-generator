package de.rk42.openapi.codegen.model.java

data class JavaParameter(
    val javaIdentifier: String,
    val location: JavaParameterLocation,
    val description: String?,
    val required: Boolean,
    val javaType: JavaReference
)