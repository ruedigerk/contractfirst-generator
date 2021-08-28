package de.rk42.openapi.codegen.model

/**
 * Represents the content of a request or response body, i.e. its media type and schema.
 */
data class CtrContent(
    val mediaType: String,
    var schema: CtrSchema
)
