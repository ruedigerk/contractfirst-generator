package client_jsr305.model;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Failure {
  @NotNull
  private Integer code;

  @NotNull
  private String message;

  public Failure code(@Nonnull Integer code) {
    this.code = code;
    return this;
  }

  @Nonnull
  public Integer getCode() {
    return code;
  }

  public void setCode(@Nonnull Integer code) {
    this.code = code;
  }

  public Failure message(@Nonnull String message) {
    this.message = message;
    return this;
  }

  @Nonnull
  public String getMessage() {
    return message;
  }

  public void setMessage(@Nonnull String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Failure o = (Failure) other;
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
    return builder.replace(0, 2, "Failure{").append('}').toString();
  }
}
