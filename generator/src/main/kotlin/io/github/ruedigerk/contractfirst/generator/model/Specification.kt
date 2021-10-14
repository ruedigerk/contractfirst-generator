package io.github.ruedigerk.contractfirst.generator.model

import io.swagger.v3.oas.models.OpenAPI

/**
 * Represents the contents of a contract.
 */
data class Specification(
    val operations: List<Operation>,
    val allSchemas: Set<ActualSchema>,
    val topLevelSchemas: Map<SchemaRef, ActualSchema>,
    val source: OpenAPI
)