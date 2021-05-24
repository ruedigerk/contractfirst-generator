package de.rk42.openapi.codegen.model.contract

import de.rk42.openapi.codegen.model.ParameterLocation

data class CtrParameter(
    val name: String,
    val location: ParameterLocation,
    val description: String,
    val required: Boolean
)