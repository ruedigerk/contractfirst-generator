package io.github.ruedigerk.contractfirst.generator.openapi

/**
 * Represents the content of a request or response body, i.e. its media type and schema.
 */
data class Content(
  val mediaType: String,
  val schemaId: SchemaId,
)
