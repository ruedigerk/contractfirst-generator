package de.rk42.openapi.codegen.model.contract

data class CtrSpecification(
    val operations: List<CtrOperation>,
    val schemas: List<CtrSchemaNonRef>
)