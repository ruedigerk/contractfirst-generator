package io.github.ruedigerk.contractfirst.generator.openapi

import io.github.ruedigerk.contractfirst.generator.parser.Parseable

/**
 * Represents the contents of a contract.
 */
data class Specification(
    val operations: List<Operation>,
    val schemas: Map<SchemaId, Schema>,
    val source: Parseable
)
