package io.github.ruedigerk.contractfirst.generator

/**
 * Thrown when the parsed contract is invalid according to the OpenAPI specification.
 */
class ParserException(val messages: List<String>) : RuntimeException(messages.joinToString(", ")) {

  constructor(msg: String) : this(listOf(msg))
}