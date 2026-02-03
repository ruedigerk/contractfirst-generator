package jsr305_server_spring.model;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty {
  @NotNull
  private String a;

  private Long b;

  public MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty a(@Nonnull String a) {
    this.a = a;
    return this;
  }

  @Nonnull
  public String getA() {
    return a;
  }

  public void setA(@Nonnull String a) {
    this.a = a;
  }

  public MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty b(@Nullable Long b) {
    this.b = b;
    return this;
  }

  @Nullable
  public Long getB() {
    return b;
  }

  public void setB(@Nullable Long b) {
    this.b = b;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty o = (MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty) other;
    return Objects.equals(a, o.a)
        && Objects.equals(b, o.b);
  }

  @Override
  public int hashCode() {
    return Objects.hash(a, b);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", a=").append(a);
    builder.append(", b=").append(b);
    return builder.replace(0, 2, "MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty{").append('}').toString();
  }
}
