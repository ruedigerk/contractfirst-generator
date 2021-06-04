package de.rk42.openapi.codegen.model.contract

sealed interface ResponseStatusCode

object DefaultStatusCode : ResponseStatusCode

data class StatusCode(val code: Int) : ResponseStatusCode