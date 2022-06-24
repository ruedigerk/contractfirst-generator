package server_jsr305.model;

import com.google.gson.annotations.SerializedName;

public enum FormEncodedRequestBodyRequestBodyFieldC {
  @SerializedName("first_value")
  FIRST_VALUE("first_value"),

  @SerializedName("second%value")
  SECOND_VALUE("second%value");

  private final String serializedName;

  FormEncodedRequestBodyRequestBodyFieldC(String serializedName) {
    this.serializedName = serializedName;
  }

  @Override
  public String toString() {
    return serializedName;
  }
}
