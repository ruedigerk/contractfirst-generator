package model_only_jsr305;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Appliance
 */
public class Appliance {
  @NotNull
  private String name;

  @Valid
  private List<@Valid Device> devices = new ArrayList<>();

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

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Appliance o = (Appliance) other;
    return Objects.equals(name, o.name)
        && Objects.equals(devices, o.devices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, devices);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", name=").append(name);
    builder.append(", devices=").append(devices);
    return builder.replace(0, 2, "Appliance{").append('}').toString();
  }
}
