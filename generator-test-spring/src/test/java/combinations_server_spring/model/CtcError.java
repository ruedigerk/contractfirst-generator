package combinations_server_spring.model;

import java.util.Objects;

public class CtcError {
  private String code;

  public CtcError code(String code) {
    this.code = code;
    return this;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    CtcError o = (CtcError) other;
    return Objects.equals(code, o.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", code=").append(code);
    return builder.replace(0, 2, "CtcError{").append('}').toString();
  }
}
