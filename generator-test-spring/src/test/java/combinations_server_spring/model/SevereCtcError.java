package combinations_server_spring.model;

import java.math.BigInteger;
import java.util.Objects;

public class SevereCtcError {
  private BigInteger code;

  public SevereCtcError code(BigInteger code) {
    this.code = code;
    return this;
  }

  public BigInteger getCode() {
    return code;
  }

  public void setCode(BigInteger code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SevereCtcError o = (SevereCtcError) other;
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
    return builder.replace(0, 2, "SevereCtcError{").append('}').toString();
  }
}
