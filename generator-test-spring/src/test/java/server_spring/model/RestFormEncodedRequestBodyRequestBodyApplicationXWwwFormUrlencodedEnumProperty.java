package server_spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RestFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty {
  @JsonProperty("first_value")
  FIRST_VALUE("first_value"),

  @JsonProperty("second%value")
  SECOND_VALUE("second%value");

  private final String serializedName;

  RestFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty(
      String serializedName) {
    this.serializedName = serializedName;
  }

  @Override
  public String toString() {
    return serializedName;
  }
}
