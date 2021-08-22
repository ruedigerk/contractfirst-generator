package de.rk42.openapi.codegen

/**
 * This exception is thrown to indicate that the code generator has detected the use of an OpenAPI feature that is not supported.
 */
class NotSupportedException(msg: String) : RuntimeException(msg) 