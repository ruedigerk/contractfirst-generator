package de.rk42.openapi.codegen.model.contract

data class CtrSpecification(
    val pathItems: List<CtrPathItem>,
    val schemas: Map<CtrSchemaRef, CtrSchema>
)