package io.github.ruedigerk.contractfirst.generator.compat_1_7_test;

import java.util.Objects;

import javax.validation.constraints.NotNull;

public class SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty {
  @NotNull
  private String a;

  private Long b;

  public SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty a(String a) {
    this.a = a;
    return this;
  }

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty b(Long b) {
    this.b = b;
    return this;
  }

  public Long getB() {
    return b;
  }

  public void setB(Long b) {
    this.b = b;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty o = (SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty) other;
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
    return builder.replace(0, 2, "SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty{").append('}').toString();
  }
}
