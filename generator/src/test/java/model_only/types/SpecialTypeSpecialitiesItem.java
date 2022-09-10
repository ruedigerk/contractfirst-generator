package model_only.types;

import java.util.Objects;

public class SpecialTypeSpecialitiesItem {
  private String label;

  private String category;

  public SpecialTypeSpecialitiesItem label(String label) {
    this.label = label;
    return this;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public SpecialTypeSpecialitiesItem category(String category) {
    this.category = category;
    return this;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
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
