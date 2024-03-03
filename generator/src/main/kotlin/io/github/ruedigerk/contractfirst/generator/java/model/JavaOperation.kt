package io.github.ruedigerk.contractfirst.generator.java.model

import io.github.ruedigerk.contractfirst.generator.model.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.model.HttpMethod
import io.github.ruedigerk.contractfirst.generator.model.StatusCode

/**
 * Represents an operation of the contract.
 */
data class JavaOperation(
    val javaMethodName: String,
    val javadoc: String?,
    val path: String,
    val httpMethod: HttpMethod,
    val requestBodyMediaType: String?,
    val parameters: List<JavaParameter>,
    val responses: List<JavaResponse>
) {

  val successTypes: Set<JavaAnyType>
  val failureTypes: Set<JavaAnyType>
  val allReturnTypes: Set<JavaAnyType>

  init {
    val directSuccessResponses = responses
        .filter { it.statusCode is StatusCode && it.statusCode.successful }
    val directFailureResponses = responses
        .filter { it.statusCode is StatusCode && !it.statusCode.successful }
    val defaultResponses = responses
        .filter { it.statusCode is DefaultStatusCode }

    // The assumption is that all success responses are specified in the contract, if any is. Therefore, the default
    // status code can only contain successful responses, when no successful responses are specified in the contract.
    val successResponses = directSuccessResponses.ifEmpty { defaultResponses }
    val failureResponses = directFailureResponses + defaultResponses

    successTypes = successResponses.getJavaTypes()
    failureTypes = failureResponses.getJavaTypes()
    allReturnTypes = successTypes + failureTypes
  }

  private fun List<JavaResponse>.getJavaTypes(): Set<JavaAnyType> = this.flatMap { it.contents }.map { it.javaType }.toSet()
}