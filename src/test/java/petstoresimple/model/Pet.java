package petstoresimple.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * The model for pets sold in the pet store.
 */
public class Pet {
  @NotNull
  private Long id;

  @NotNull
  private String name;

  private String tag;

  /**
   * An enum for each species.
   */
  private Species species;

  private PetMultiplier multiplier;

  private PetGender gender;

  public Pet id(Long id) {
    this.id = id;
    return this;
  }

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

  public Pet multiplier(PetMultiplier multiplier) {
    this.multiplier = multiplier;
    return this;
  }

  public PetMultiplier getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(PetMultiplier multiplier) {
    this.multiplier = multiplier;
  }

  public Pet gender(PetGender gender) {
    this.gender = gender;
    return this;
  }

  public PetGender getGender() {
    return gender;
  }

  public void setGender(PetGender gender) {
    this.gender = gender;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Pet o = (Pet) other;
    return Objects.equals(id, o.id)
        && Objects.equals(name, o.name)
        && Objects.equals(tag, o.tag)
        && Objects.equals(species, o.species)
        && Objects.equals(multiplier, o.multiplier)
        && Objects.equals(gender, o.gender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, tag, species, multiplier, gender);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", id=").append(id);
    builder.append(", name=").append(name);
    builder.append(", tag=").append(tag);
    builder.append(", species=").append(species);
    builder.append(", multiplier=").append(multiplier);
    builder.append(", gender=").append(gender);
    return builder.replace(0, 2, "Pet{").append('}').toString();
  }
}
