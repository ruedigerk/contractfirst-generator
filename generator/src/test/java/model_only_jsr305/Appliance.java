package model_only_jsr305;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import model_only_jsr305.types.SpecialType;

/**
 * Appliance
 */
public class Appliance {
  @NotNull
  private String name;

  @Valid
  private List<@Valid Device> devices = new ArrayList<>();

  /**
   * Special Type
   */
  @Valid
  private SpecialType type;

  public Appliance name(@Nonnull String name) {
    this.name = name;
    return this;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  public Appliance devices(@Nullable List<Device> devices) {
    this.devices = devices;
    return this;
  }

  @Nullable
  public List<Device> getDevices() {
    return devices;
  }

  public void setDevices(@Nullable List<Device> devices) {
    this.devices = devices;
  }

  public Appliance type(@Nullable SpecialType type) {
    this.type = type;
    return this;
  }

  @Nullable
  public SpecialType getType() {
    return type;
  }

  public void setType(@Nullable SpecialType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Appliance o = (Appliance) other;
    return Objects.equals(name, o.name)
        && Objects.equals(devices, o.devices)
        && Objects.equals(type, o.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, devices, type);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", name=").append(name);
    builder.append(", devices=").append(devices);
    builder.append(", type=").append(type);
    return builder.replace(0, 2, "Appliance{").append('}').toString();
  }
}
