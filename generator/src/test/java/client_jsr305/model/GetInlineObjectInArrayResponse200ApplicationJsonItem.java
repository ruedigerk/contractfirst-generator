package client_jsr305.model;

import java.util.Objects;
import javax.annotation.Nullable;

public class GetInlineObjectInArrayResponse200ApplicationJsonItem {
  private String someProperty;

  public GetInlineObjectInArrayResponse200ApplicationJsonItem someProperty(
      @Nullable String someProperty) {
    this.someProperty = someProperty;
    return this;
  }

  @Nullable
  public String getSomeProperty() {
    return someProperty;
  }

  public void setSomeProperty(@Nullable String someProperty) {
    this.someProperty = someProperty;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    GetInlineObjectInArrayResponse200ApplicationJsonItem o = (GetInlineObjectInArrayResponse200ApplicationJsonItem) other;
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
    return builder.replace(0, 2, "GetInlineObjectInArrayResponse200ApplicationJsonItem{").append('}').toString();
  }
}
