package parameters_server.model;

import java.util.Objects;

public class GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded {
  private String theParameter;

  private String other;

  public GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded theParameter(
      String theParameter) {
    this.theParameter = theParameter;
    return this;
  }

  public String getTheParameter() {
    return theParameter;
  }

  public void setTheParameter(String theParameter) {
    this.theParameter = theParameter;
  }

  public GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded other(String other) {
    this.other = other;
    return this;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  @Override
  public boolean equals(Object other_) {
    if (other_ == this) return true;
    if (other_ == null || getClass() != other_.getClass()) return false;
    GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded o = (GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded) other_;
    return Objects.equals(theParameter, o.theParameter)
        && Objects.equals(other, o.other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(theParameter, other);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", theParameter=").append(theParameter);
    builder.append(", other=").append(other);
    return builder.replace(0, 2, "GetEquallyNamedParametersRequestBodyApplicationXWwwFormUrlencoded{").append('}').toString();
  }
}
