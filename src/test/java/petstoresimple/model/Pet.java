package petstoresimple.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class Pet {
  private Long id;

  private String name;

  private String tag;

  private Species species;

  public Pet id(Long id) {
    this.id = id;
    return this;
  }

  @NotNull
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Pet name(String name) {
    this.name = name;
    return this;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Pet tag(String tag) {
    this.tag = tag;
    return this;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public Pet species(Species species) {
    this.species = species;
    return this;
  }

  public Species getSpecies() {
    return species;
  }

  public void setSpecies(Species species) {
    this.species = species;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Pet o = (Pet) other;
    return Objects.equals(id, o.id)
        && Objects.equals(name, o.name)
        && Objects.equals(tag, o.tag)
        && Objects.equals(species, o.species);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, tag, species);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", id=").append(id);
    builder.append(", name=").append(name);
    builder.append(", tag=").append(tag);
    builder.append(", species=").append(species);
    return builder.replace(0, 2, "Pet{").append('}').toString();
  }
}
