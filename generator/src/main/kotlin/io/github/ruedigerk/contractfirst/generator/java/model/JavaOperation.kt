package io.github.ruedigerk.contractfirst.generator.java.model

import io.github.ruedigerk.contractfirst.generator.model.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.model.StatusCode

/**
 * Represents an operation of the contract.
 */
data class JavaOperation(
    val javaIdentifier: String,
    val javadoc: String?,
    val path: String,
    val method: String,
    val requestBodyMediaTypes: List<String>,
    val parameters: List<JavaParameter>,
    val responses: List<JavaResponse>
) {

  /**
   * The assumption is that all success responses are specified in the contract, if any is. Therefore, the default
   * status code can only contain successful responses, when no successful responses are specified in the contract.
   */
  fun responseTypesBySuccess(): ResponseTypesBySuccess {
    val successResponses = responses
        .filter { it.statusCode is StatusCode && it.statusCode.successful }
    val failureResponses = responses
        .filter { it.statusCode is StatusCode && !it.statusCode.successful }
    val defaultResponses = responses
        .filter { it.statusCode is DefaultStatusCode }

    val effectiveSuccessResponses = successResponses.ifEmpty { defaultResponses }
    val effectiveFailureResponses = failureResponses + defaultResponses

    return ResponseTypesBySuccess(effectiveSuccessResponses.getJavaTypes(), effectiveFailureResponses.getJavaTypes())
  }

  private fun List<JavaResponse>.getJavaTypes(): List<JavaAnyType> = this.flatMap { it.contents }.map { it.javaType }.distinct()

  data class ResponseTypesBySuccess(
      val successTypes: List<JavaAnyType>,
      val failureTypes: List<JavaAnyType>,
  )
}