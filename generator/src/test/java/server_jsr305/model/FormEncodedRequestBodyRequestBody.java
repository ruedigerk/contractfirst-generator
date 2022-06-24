package server_jsr305.model;

import java.util.Objects;
import javax.annotation.Nullable;

public class FormEncodedRequestBodyRequestBody {
  private String fieldA;

  private String fieldB;

  private FormEncodedRequestBodyRequestBodyFieldC fieldC;

  public FormEncodedRequestBodyRequestBody fieldA(@Nullable String fieldA) {
    this.fieldA = fieldA;
    return this;
  }

  @Nullable
  public String getFieldA() {
    return fieldA;
  }

  public void setFieldA(@Nullable String fieldA) {
    this.fieldA = fieldA;
  }

  public FormEncodedRequestBodyRequestBody fieldB(@Nullable String fieldB) {
    this.fieldB = fieldB;
    return this;
  }

  @Nullable
  public String getFieldB() {
    return fieldB;
  }

  public void setFieldB(@Nullable String fieldB) {
    this.fieldB = fieldB;
  }

  public FormEncodedRequestBodyRequestBody fieldC(
      @Nullable FormEncodedRequestBodyRequestBodyFieldC fieldC) {
    this.fieldC = fieldC;
    return this;
  }

  @Nullable
  public FormEncodedRequestBodyRequestBodyFieldC getFieldC() {
    return fieldC;
  }

  public void setFieldC(@Nullable FormEncodedRequestBodyRequestBodyFieldC fieldC) {
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
