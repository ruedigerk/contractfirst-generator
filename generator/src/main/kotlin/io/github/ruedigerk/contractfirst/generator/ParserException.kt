package io.github.ruedigerk.contractfirst.generator

/**
 * Thrown when the parsed contract cannot be parsed, either because it is invalid according to the OpenAPI specification, or because there was an IO error.
 */
abstract class ParserException(
  message: String,
  cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Thrown when there was a problem with the contents of the contract to parse.
 */
class ParserContentException(
  message: String,
  cause: Throwable? = null,
) : ParserException(message, cause)

/**
 * Thrown when an IO error occurs during paring of the contract.
 */
open class ParserIoException(
  message: String,
  cause: Throwable? = null,
) : ParserException(message, cause)

/**
 * Thrown when a referenced file was not found during parsing of the contract.
 */
class ParserFileNotFoundException(
  message: String,
  cause: Throwable? = null,
) : ParserIoException(message, cause)
