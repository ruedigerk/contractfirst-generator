package io.github.ruedigerk.contractfirst.generator.model

sealed interface ResponseStatusCode

object DefaultStatusCode : ResponseStatusCode

data class StatusCode(val code: Int) : ResponseStatusCode {

  val successful: Boolean
    get() = code in 200..299
}