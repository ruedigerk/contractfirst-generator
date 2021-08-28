package de.rk42.openapi.codegen.java.model

/**
 * Represents the content of a request or response body, i.e. its media type and its entity's Java type.
 */
data class JavaContent(
    val mediaType: String,
    var javaType: JavaAnyType
)
