package de.rk42.openapi.codegen.parser

class ParserException(val messages: List<String>) : RuntimeException() {

  constructor(msg: String) : this(listOf(msg))
}