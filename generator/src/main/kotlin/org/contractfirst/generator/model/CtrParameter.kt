package org.contractfirst.generator.model

data class CtrParameter(
    val name: String,
    val location: ParameterLocation,
    val description: String?,
    val required: Boolean,
    var schema: CtrSchema
)