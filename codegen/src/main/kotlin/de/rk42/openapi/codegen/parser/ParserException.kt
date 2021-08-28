package de.rk42.openapi.codegen.parser

/**
 * Thrown when the parsed contract is invalid according to the OpenAPI specification.
 */
class ParserException(val messages: List<String>) : RuntimeException() {

  constructor(msg: String) : this(listOf(msg))
}