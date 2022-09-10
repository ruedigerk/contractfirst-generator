package model_only.sibling;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Sibling
 */
public class Sibling {
  private BigInteger value;

  public Sibling value(BigInteger value) {
    this.value = value;
    return this;
  }

  public BigInteger getValue() {
    return value;
  }

  public void setValue(BigInteger value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Sibling o = (Sibling) other;
    return Objects.equals(value, o.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", value=").append(value);
    return builder.replace(0, 2, "Sibling{").append('}').toString();
  }
}
