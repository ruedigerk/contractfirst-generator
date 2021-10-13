package io.github.ruedigerk.contractfirst.generator.client;

import java.util.Objects;

/**
 * Represents an HTTP header.
 */
public class Header {

  private final String name;
  private final String value;

  public Header(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * The name of the header, e.g., "Content-Type".
   */
  public String getName() {
    return name;
  }

  /**
   * The value of the header, e.g., "application/json".
   */
  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Header that = (Header) o;
    return Objects.equals(name, that.name) &&
           Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public String toString() {
    return name + "=" + value;
  }
}
