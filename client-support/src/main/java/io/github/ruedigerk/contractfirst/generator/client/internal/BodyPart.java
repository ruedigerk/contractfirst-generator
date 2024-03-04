package io.github.ruedigerk.contractfirst.generator.client.internal;

/**
 * A single part of a multipart request body, or a single form field for an application/x-www-form-urlencoded request body.
 */
public class BodyPart {

  private final Type type;
  private final String name;
  private final Object value;

  public BodyPart(Type type, String name, Object value) {
    this.type = type;
    this.name = name;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }

  /**
   * Types of body parts as specified by OAS: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   */
  public enum Type {

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
