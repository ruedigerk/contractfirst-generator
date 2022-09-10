package model_only_jsr305.types;

import java.util.Objects;
import javax.annotation.Nullable;

public class SpecialTypeSpecialitiesItem {
  private String label;

  private String category;

  public SpecialTypeSpecialitiesItem label(@Nullable String label) {
    this.label = label;
    return this;
  }

  @Nullable
  public String getLabel() {
    return label;
  }

  public void setLabel(@Nullable String label) {
    this.label = label;
  }

  public SpecialTypeSpecialitiesItem category(@Nullable String category) {
    this.category = category;
    return this;
  }

  @Nullable
  public String getCategory() {
    return category;
  }

  public void setCategory(@Nullable String category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SpecialTypeSpecialitiesItem o = (SpecialTypeSpecialitiesItem) other;
    return Objects.equals(label, o.label)
        && Objects.equals(category, o.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, category);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", label=").append(label);
    builder.append(", category=").append(category);
    return builder.replace(0, 2, "SpecialTypeSpecialitiesItem{").append('}').toString();
  }
}
