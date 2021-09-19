package org.contractfirst.generator.model

import io.swagger.v3.oas.models.OpenAPI

/**
 * Represents the contents of a contract.
 */
data class MSpecification(
    val operations: List<MOperation>,
    val schemas: List<MSchemaNonRef>,
    val source: OpenAPI
)