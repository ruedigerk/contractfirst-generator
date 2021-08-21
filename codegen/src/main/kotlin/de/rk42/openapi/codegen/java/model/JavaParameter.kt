package de.rk42.openapi.codegen.java.model

data class JavaParameter(
    val javaIdentifier: String,
    val javadoc: String?,
    val location: JavaParameterLocation,
    val required: Boolean,
    val javaType: JavaAnyType
)