package server.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class Error {
  @NotNull
  private Integer code;

  @NotNull
  private String message;

  public Error code(Integer code) {
    this.code = code;
    return this;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Error message(String message) {
    this.message = message;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Error o = (Error) other;
    return Objects.equals(code, o.code)
        && Objects.equals(message, o.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", code=").append(code);
    builder.append(", message=").append(message);
    return builder.replace(0, 2, "Error{").append('}').toString();
  }
}
