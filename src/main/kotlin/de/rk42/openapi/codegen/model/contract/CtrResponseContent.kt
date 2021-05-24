package de.rk42.openapi.codegen.model.contract

data class CtrResponseContent(
    val mediaType: String,
    val schema: CtrSchemaRef
)
