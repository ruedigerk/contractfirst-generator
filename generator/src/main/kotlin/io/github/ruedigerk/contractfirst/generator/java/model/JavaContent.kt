package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents the content of a request or response body, i.e. its media type and its entity's Java type.
 */
data class JavaContent(
    val mediaType: String,
    val javaType: JavaAnyType
)
