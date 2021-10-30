package server.model;

import java.util.Objects;

public class FormEncodedRequestBodyRequestBody {
  private String fieldA;

  private String fieldB;

  public FormEncodedRequestBodyRequestBody fieldA(String fieldA) {
    this.fieldA = fieldA;
    return this;
  }

  public String getFieldA() {
    return fieldA;
  }

  public void setFieldA(String fieldA) {
    this.fieldA = fieldA;
  }

  public FormEncodedRequestBodyRequestBody fieldB(String fieldB) {
    this.fieldB = fieldB;
    return this;
  }

  public String getFieldB() {
    return fieldB;
  }

  public void setFieldB(String fieldB) {
    this.fieldB = fieldB;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    FormEncodedRequestBodyRequestBody o = (FormEncodedRequestBodyRequestBody) other;
    return Objects.equals(fieldA, o.fieldA)
        && Objects.equals(fieldB, o.fieldB);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldA, fieldB);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", fieldA=").append(fieldA);
    builder.append(", fieldB=").append(fieldB);
    return builder.replace(0, 2, "FormEncodedRequestBodyRequestBody{").append('}').toString();
  }
}
