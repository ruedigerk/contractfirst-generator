package server_spring.model;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class RestItem {
  @NotNull
  private Long id;

  @NotNull
  private String name;

  private String tag;

  public RestItem id(Long id) {
    this.id = id;
    return this;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public RestItem name(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RestItem tag(String tag) {
    this.tag = tag;
    return this;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RestItem o = (RestItem) other;
    return Objects.equals(id, o.id)
        && Objects.equals(name, o.name)
        && Objects.equals(tag, o.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, tag);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", id=").append(id);
    builder.append(", name=").append(name);
    builder.append(", tag=").append(tag);
    return builder.replace(0, 2, "RestItem{").append('}').toString();
  }
}
