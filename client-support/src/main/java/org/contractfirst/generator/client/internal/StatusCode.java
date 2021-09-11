package org.contractfirst.generator.client.internal;

import java.util.Objects;

/**
 * Represents the status code of a response of an API operation. The status code can either be a real numeric HTTP status code or the "default" status
 * code, a placeholder for not otherwise specified status codes.
 */
public class StatusCode {

  public static final StatusCode DEFAULT = new StatusCode(null);

  private final Integer code;

  private StatusCode(Integer code) {
    this.code = code;
  }

  public static StatusCode of(int statusCode) {
    return new StatusCode(statusCode);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StatusCode that = (StatusCode) o;
    return Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public String toString() {
    if (code == null) {
      return "default";
    } else {
      return code.toString();
    }
  }
}
