package de.rk42.openapi.codegen.model.java

import de.rk42.openapi.codegen.model.ParameterLocation

sealed interface JavaParameterLocation

object JavaBodyParameter : JavaParameterLocation

data class JavaRegularParameterLocation(
    val name: String,
    val location: ParameterLocation,
) : JavaParameterLocation