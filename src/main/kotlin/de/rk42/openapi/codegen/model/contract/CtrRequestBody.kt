package de.rk42.openapi.codegen.model.contract

data class CtrRequestBody(
    val description: String?,
    val required: Boolean,
    val contents: List<CtrContent>
)