package io.github.ruedigerk.contractfirst.generator

/**
 * Thrown when the parsed contract cannot be parsed, either because it is invalid according to the OpenAPI specification, or because there was an IO error.
 */
abstract class ParserException : RuntimeException {

  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)

  companion object {
    
    operator fun invoke(messages: List<String>): ParserException = ParserContentException(messages.joinToString(", "))
  }
}

class ParserContentException : ParserException {

  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

open class ParserIoException : ParserException {

  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

class ParserFileNotFoundException : ParserIoException {

  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}