package de.rk42.openapi.codegen.model.java

import de.rk42.openapi.codegen.model.ParameterLocation

data class JavaParameter(
    val javaIdentifier: String,
    val name: String,
    val location: ParameterLocation,
    val description: String,
    val required: Boolean,
    val javaType: JavaReference
)