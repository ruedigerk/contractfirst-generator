package model_only;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

/**
 * Appliance
 */
public class Appliance {
  private String name;

  @Valid
  private List<@Valid Device> devices = new ArrayList<>();

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
