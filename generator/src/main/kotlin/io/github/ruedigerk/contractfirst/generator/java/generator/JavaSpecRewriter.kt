package io.github.ruedigerk.contractfirst.generator.java.generator

import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaCollectionType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaContent
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.ATTACHMENT
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily.FORM_URL_ENCODED
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperationGroup
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaResponse
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.java.model.JavaType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName

/**
 * Helps in rewriting a JavaSpecification, especially the parameters and response contents, and their types.
 */
class JavaSpecRewriter(
  private val operationRewriters: List<Rewriter<JavaOperation>> = listOf(),
  private val parameterRewriters: List<Rewriter<JavaParameter>> = listOf(),
  private val responseContentRewriters: List<Rewriter<JavaContent>> = listOf(),
) : (JavaSpecification) -> JavaSpecification {

  override operator fun invoke(specification: JavaSpecification): JavaSpecification {
    val operationGroups = specification.operationGroups.map(::rewriteOperationGroup)
    return specification.copy(operationGroups = operationGroups)
  }

  private fun rewriteOperationGroup(operationGroup: JavaOperationGroup): JavaOperationGroup {
    val operations = operationGroup.operations.map(::rewriteOperation)
    return operationGroup.copy(operations = operations)
  }

  private fun rewriteOperation(operation: JavaOperation): JavaOperation {
    val rewrittenOperation = operationRewriters.rewrite(operation)
    val parameters = rewrittenOperation.parameters.map { parameterRewriters.rewrite(it) }
    val responses = rewrittenOperation.responses.map(::rewriteResponse)
    return rewrittenOperation.copy(parameters = parameters, responses = responses)
  }

  private fun rewriteResponse(response: JavaResponse): JavaResponse {
    val contents = response.contents.map { responseContentRewriters.rewrite(it) }
    return response.copy(contents = contents)
  }

  private fun <T> List<Rewriter<T>>.rewrite(subject: T): T = fold(subject) { subject, rewriter -> rewriter(subject) }

  /**
   * Contains [Rewriter]s and functions to compose [Rewriter]s with, to be used with [JavaSpecRewriter].
   */
  companion object {

    /**
     * A Rewriter is a function that transforms an input of some type T to an instance of the same type. Usually, a rewriter performs a specific
     * transformation, like replacing a specific type used in a specific location of the contract, while leaving everything else as before.
     */
    typealias Rewriter<T> = T.() -> T

    /**
     * Rewrites the types of body parameters using the supplied type rewriter.
     */
    fun rewriteBodyParameterType(typeRewriter: Rewriter<JavaAnyType>): Rewriter<JavaParameter> = {
      when (this) {
        is JavaBodyParameter -> copy(javaType = typeRewriter(javaType))
        else -> this
      }
    }

    /**
     * Rewrites the types of dissected body parameters using the supplied type rewriter.
     */
    fun rewriteDissectedBodyParameterType(typeRewriter: Rewriter<JavaAnyType>): Rewriter<JavaParameter> = {
      when (this) {
        is JavaDissectedBodyParameter -> copy(javaType = typeRewriter(javaType))
        else -> this
      }
    }

    /**
     * Rewrites the types of dissected body parameters using the supplied type rewriter, if the media type of the body matches of a specified family.
     */
    fun rewriteDissectedBodyParameter(family: DissectedMediaTypeFamily, rewriter: Rewriter<JavaDissectedBodyParameter>): Rewriter<JavaParameter> = {
      when (this) {
        is JavaDissectedBodyParameter if (dissectedMediaTypeFamily == family) -> rewriter(this)
        else -> this
      }
    }

    /**
     * Rewrites the types of response contents using the supplied type rewriter.
     */
    fun rewriteResponseContentType(typeRewriter: Rewriter<JavaAnyType>): Rewriter<JavaContent> = {
      val replacementType = typeRewriter(javaType)
      copy(javaType = replacementType)
    }

    /**
     * Rewrites types that represent binary data ("java.io.InputStream" by default) to the specified type.
     */
    fun rewriteBinaryTypeTo(replacement: JavaTypeName): Rewriter<JavaAnyType> = {
      when (this) {
        is JavaType if (name == JavaTypeName.INPUT_STREAM) -> copy(name = replacement)

        is JavaCollectionType if (elementType is JavaType && elementType.name == JavaTypeName.INPUT_STREAM) -> copy(
          elementType = elementType.copy(name = replacement),
        )

        else -> this
      }
    }

    /**
     * Rewrites types of form URL-encoded request body parameters. Generated types and binary types are rewritten to String.
     */
    val rewriteFormUrlEncodedBodyParameters: Rewriter<JavaParameter> = rewriteDissectedBodyParameter(FORM_URL_ENCODED) {
      when {
        bodyPartType == ATTACHMENT || javaType.isGenerated() -> copy(javaType = JavaType(JavaTypeName.STRING))
        else -> this
      }
    }
  }
}
