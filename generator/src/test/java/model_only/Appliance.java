package model_only;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model_only.types.SpecialType;

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

  public Appliance name(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Appliance devices(List<Device> devices) {
    this.devices = devices;
    return this;
  }

  public List<Device> getDevices() {
    return devices;
  }

  public void setDevices(List<Device> devices) {
    this.devices = devices;
  }

  public Appliance type(SpecialType type) {
    this.type = type;
    return this;
  }

  public SpecialType getType() {
    return type;
  }

  public void setType(SpecialType type) {
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
