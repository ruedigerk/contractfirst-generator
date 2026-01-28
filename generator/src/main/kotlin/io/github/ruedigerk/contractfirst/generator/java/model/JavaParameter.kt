package io.github.ruedigerk.contractfirst.generator.java.model

import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation

/**
 * Represents a parameter of an operation of the contract.
 */
sealed interface JavaParameter {

  val javaParameterName: String
  val javadoc: String?
  val required: Boolean
  val javaType: JavaAnyType

  fun isCookieParameter(): Boolean = this is JavaRegularParameter && this.location == ParameterLocation.COOKIE
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
  val originalName: String,
) : JavaParameter

/**
 * Represents the single body parameter of an operation of the contract.
 */
data class JavaBodyParameter(
  override val javaParameterName: String,
  override val javadoc: String?,
  override val required: Boolean,
  override val javaType: JavaAnyType,
  val mediaType: String,
) : JavaParameter

/**
 * Represents a form field or multipart part of a form or multipart request body.
 *
 * Note: the Java type for binary parameters/attachments is framework-specific and may be generated differently from the javaType property here.
 */
data class JavaMultipartBodyParameter(
  override val javaParameterName: String,
  override val javadoc: String?,
  override val required: Boolean,
  override val javaType: JavaAnyType,
  val originalName: String,
  val bodyPartType: BodyPartType,
) : JavaParameter {

  /**
   * This needs to be in sync with enum "BodyPart.Type" of the client support module!
   *
   * See: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   */
  enum class BodyPartType {

    /**
     * The body part is a primitive, like String. Note that the body part can also represent an array of primitives.
     */
    PRIMITIVE,

    /**
     * The body part is complex, meaning it is to be treated as JSON to serialize/deserialize into, e.g., Java objects. Note that the body part can also
     * represent an array of JSON entities.
     */
    COMPLEX,

    /**
     * The body part is binary data, like a file, and typically needs framework-specific handling. Note that the body part can also represent an array of
     * binaries.
     */
    ATTACHMENT,
  }
}
