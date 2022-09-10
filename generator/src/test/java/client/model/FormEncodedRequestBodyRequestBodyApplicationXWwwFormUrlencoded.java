package client.model;

import java.util.Objects;

public class FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded {
  private String fieldA;

  private String fieldB;

  private FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedFieldC fieldC;

  public FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded fieldA(String fieldA) {
    this.fieldA = fieldA;
    return this;
  }

  public String getFieldA() {
    return fieldA;
  }

  public void setFieldA(String fieldA) {
    this.fieldA = fieldA;
  }

  public FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded fieldB(String fieldB) {
    this.fieldB = fieldB;
    return this;
  }

  public String getFieldB() {
    return fieldB;
  }

  public void setFieldB(String fieldB) {
    this.fieldB = fieldB;
  }

  public FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded fieldC(
      FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedFieldC fieldC) {
    this.fieldC = fieldC;
    return this;
  }

  public FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedFieldC getFieldC() {
    return fieldC;
  }

  public void setFieldC(
      FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedFieldC fieldC) {
    this.fieldC = fieldC;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded o = (FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded) other;
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
    return builder.replace(0, 2, "FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencoded{").append('}').toString();
  }
}
