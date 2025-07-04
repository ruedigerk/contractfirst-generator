package io.github.ruedigerk.contractfirst.generator.client.internal

/**
 * A single part of a multipart request body, or a single form field for an application/x-www-form-urlencoded request body.
 */
data class BodyPart(

  /**
   * The type of the body part.
   *
   * BACKWARDS_COMPATIBILITY(1.7): This is nullable only for backwards compatibility with code generated by versions <= 1.7.
   */
  val type: Type?,

  val name: String,

  /**
   * The value of the body part. Since version 1.8 this has to be [io.github.ruedigerk.contractfirst.generator.client.Attachment] for body parts of type
   * [Type.ATTACHMENT].
   * Since version 1.8.0 this is nullable to support optional body parts.
   */
  val value: Any?,
) {

  /**
   * Types of body parts as specified by OAS: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   */
  enum class Type {

    /**
     * A primitive body part, like a string. Is serialized like a query parameter by default.
     */
    PRIMITIVE,

    /**
     * A complex body part that is serialized like a JSON request body.
     */
    COMPLEX,

    /**
     * A binary/file body part that is serialized like an attachment, with a file name and a media type.
     */
    ATTACHMENT
  }
}
