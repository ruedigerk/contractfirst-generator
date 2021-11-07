package client.model;

import java.util.Objects;

public class FormEncodedRequestBodyRequestBody {
  private String fieldA;

  private String fieldB;

  private FormEncodedRequestBodyRequestBodyFieldC fieldC;

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

  public FormEncodedRequestBodyRequestBody fieldC(FormEncodedRequestBodyRequestBodyFieldC fieldC) {
    this.fieldC = fieldC;
    return this;
  }

  public FormEncodedRequestBodyRequestBodyFieldC getFieldC() {
    return fieldC;
  }

  public void setFieldC(FormEncodedRequestBodyRequestBodyFieldC fieldC) {
    this.fieldC = fieldC;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    FormEncodedRequestBodyRequestBody o = (FormEncodedRequestBodyRequestBody) other;
    return Objects.equals(fieldA, o.fieldA)
        && Objects.equals(fieldB, o.fieldB)
        && Objects.equals(fieldC, o.fieldC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldA, fieldB, fieldC);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", fieldA=").append(fieldA);
    builder.append(", fieldB=").append(fieldB);
    builder.append(", fieldC=").append(fieldC);
    return builder.replace(0, 2, "FormEncodedRequestBodyRequestBody{").append('}').toString();
  }
}
