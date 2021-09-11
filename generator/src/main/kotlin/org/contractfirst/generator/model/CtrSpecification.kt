package org.contractfirst.generator.model

import io.swagger.v3.oas.models.OpenAPI

data class CtrSpecification(
    val operations: List<CtrOperation>,
    val schemas: List<CtrSchemaNonRef>,
    val source: OpenAPI
)