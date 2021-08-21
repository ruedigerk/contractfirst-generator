package petstoresimple.model;

import java.util.Objects;

public class GetPetTransformer200 {
  private String someProperty;

  public GetPetTransformer200 someProperty(String someProperty) {
    this.someProperty = someProperty;
    return this;
  }

  public String getSomeProperty() {
    return someProperty;
  }

  public void setSomeProperty(String someProperty) {
    this.someProperty = someProperty;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    GetPetTransformer200 o = (GetPetTransformer200) other;
    return Objects.equals(someProperty, o.someProperty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(someProperty);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", someProperty=").append(someProperty);
    return builder.replace(0, 2, "GetPetTransformer200{").append('}').toString();
  }
}
