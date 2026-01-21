package model_only_jsr305.types;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import model_only_jsr305.sibling.Sibling;

/**
 * Special Type
 */
public class SpecialType {
  /**
   * Sibling
   */
  @Valid
  private Sibling sibling;

  private String typeName;

  @Valid
  private List<@Valid SpecialTypeSpecialitiesItem> specialities = new ArrayList<>();

  public SpecialType sibling(@Nullable Sibling sibling) {
    this.sibling = sibling;
    return this;
  }

  @Nullable
  public Sibling getSibling() {
    return sibling;
  }

  public void setSibling(@Nullable Sibling sibling) {
    this.sibling = sibling;
  }

  public SpecialType typeName(@Nullable String typeName) {
    this.typeName = typeName;
    return this;
  }

  @Nullable
  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(@Nullable String typeName) {
    this.typeName = typeName;
  }

  public SpecialType specialities(@Nullable List<SpecialTypeSpecialitiesItem> specialities) {
    this.specialities = specialities;
    return this;
  }

  @Nullable
  public List<SpecialTypeSpecialitiesItem> getSpecialities() {
    return specialities;
  }

  public void setSpecialities(@Nullable List<SpecialTypeSpecialitiesItem> specialities) {
    this.specialities = specialities;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SpecialType o = (SpecialType) other;
    return Objects.equals(sibling, o.sibling)
        && Objects.equals(typeName, o.typeName)
        && Objects.equals(specialities, o.specialities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sibling, typeName, specialities);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", sibling=").append(sibling);
    builder.append(", typeName=").append(typeName);
    builder.append(", specialities=").append(specialities);
    return builder.replace(0, 2, "SpecialType{").append('}').toString();
  }
}
