package model_only_jsr305;

import java.math.BigDecimal;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Device
 */
public class Device {
  private String name;

  private BigDecimal value;

  public Device name(@Nullable String name) {
    this.name = name;
    return this;
  }

  @Nullable
  public String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public Device value(@Nullable BigDecimal value) {
    this.value = value;
    return this;
  }

  @Nullable
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(@Nullable BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Device o = (Device) other;
    return Objects.equals(name, o.name)
        && Objects.equals(value, o.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", name=").append(name);
    builder.append(", value=").append(value);
    return builder.replace(0, 2, "Device{").append('}').toString();
  }
}
