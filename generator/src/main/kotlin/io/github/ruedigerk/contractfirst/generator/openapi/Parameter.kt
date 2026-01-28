package io.github.ruedigerk.contractfirst.generator.openapi

/**
 * Represents a parameter of an operation in the contract.
 */
data class Parameter(
  val name: String,
  val location: ParameterLocation,
  val description: String?,
  val required: Boolean,
  val schema: SchemaId,
)
