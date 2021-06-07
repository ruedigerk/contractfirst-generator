package de.rk42.openapi.codegen.model

data class CtrRequestBody(
    val description: String?,
    val required: Boolean,
    val contents: List<CtrContent>
)