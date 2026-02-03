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
 * A parameter that is identified by a name in the OpenAPI contract and during HTTP transport.
 */
sealed interface JavaNamedParameter : JavaParameter {

  /**
   * The name of the parameter as defined in the OpenAPI contract and as it is used during HTTP transport.
   */
  val originalName: String
}

/**
 * Represents a path, query, header or cookie parameter of an operation of the contract.
 */
data class JavaRegularParameter(
  override val javaParameterName: String,
  override val javadoc: String?,
  override val required: Boolean,
  override val javaType: JavaAnyType,
  override val originalName: String,
  val location: ParameterLocation,
) : JavaNamedParameter

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
 * Represents a parameter that results from dissecting the parts (form fields, or multipart parts) of a request body into individual parameters.
 *
 * See: https://spec.openapis.org/oas/v3.0.3#support-for-x-www-form-urlencoded-request-bodies
 * See: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
 */
data class JavaDissectedBodyParameter(
  override val javaParameterName: String,
  override val javadoc: String?,
  override val required: Boolean,
  override val javaType: JavaAnyType,
  override val originalName: String,
  val bodyPartType: BodyPartType,
  val dissectedMediaTypeFamily: DissectedMediaTypeFamily,
) : JavaNamedParameter {

  /**
   * This specifies the category of the type of a single part of a dissected body falls into.
   *
   * See: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   *
   * Note: This needs to be in sync with enum "BodyPart.Type" of the client support module!
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

  /**
   * This enumeration represents the family of media types a dissected body part belongs to.
   *
   * Background: OpenAPI distinguishes two different families of media types that result in a request body to be handled as dissected into individual
   * parts: form URL-encoded bodies (application/x-www-form-urlencoded) and multipart bodies (media types with a type of "multipart" and any subtype).
   */
  enum class DissectedMediaTypeFamily {
    FORM_URL_ENCODED,
    MULTIPART,
  }
}
