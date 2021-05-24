package de.rk42.openapi.codegen

/**
 * Thrown when the input contract contains something invalid according to the OpenAPI specification.
 */
class InvalidContractException(msg: String) : RuntimeException(msg)
