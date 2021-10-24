package io.github.ruedigerk.contractfirst.generator.java.model

import io.github.ruedigerk.contractfirst.generator.model.ParameterLocation

/**
 * Represents a parameter of an operation of the contract.
 */
sealed interface JavaParameter {

  val javaParameterName: String
  val javadoc: String?
  val required: Boolean
  val javaType: JavaAnyType
}

/**
 * Represents a path, query, header or cookie parameter of an operation of the contract.
 */
data class JavaRegularParameter(
    override val javaParameterName: String,
    override val javadoc: String?,
    override val required: Boolean,
    override val javaType: JavaAnyType,
    val location: ParameterLocation,
    val name: String
) : JavaParameter

/**
 * Represents the single body parameter of an operation of the contract.
 */
data class JavaBodyParameter(
    override val javaParameterName: String,
    override val javadoc: String?,
    override val required: Boolean,
    override val javaType: JavaAnyType,
    val mediaType: String
) : JavaParameter