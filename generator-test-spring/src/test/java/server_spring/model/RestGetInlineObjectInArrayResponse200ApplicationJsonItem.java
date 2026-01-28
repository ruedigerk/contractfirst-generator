package server_spring.model;

import java.util.Objects;

public class RestGetInlineObjectInArrayResponse200ApplicationJsonItem {
  private String someProperty;

  public RestGetInlineObjectInArrayResponse200ApplicationJsonItem someProperty(
      String someProperty) {
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
    RestGetInlineObjectInArrayResponse200ApplicationJsonItem o = (RestGetInlineObjectInArrayResponse200ApplicationJsonItem) other;
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
    return builder.replace(0, 2, "RestGetInlineObjectInArrayResponse200ApplicationJsonItem{").append('}').toString();
  }
}
