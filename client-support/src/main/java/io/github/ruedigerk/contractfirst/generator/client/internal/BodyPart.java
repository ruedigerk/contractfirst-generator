package io.github.ruedigerk.contractfirst.generator.client.internal;

/**
 * A single part of a multipart request body, or a single form field for an application/x-www-form-urlencoded request body.
 */
public class BodyPart {

  private final String name;
  private final Object value;

  public BodyPart(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }
}
