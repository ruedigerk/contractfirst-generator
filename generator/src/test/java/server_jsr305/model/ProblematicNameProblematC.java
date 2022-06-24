package server_jsr305.model;

import com.google.gson.annotations.SerializedName;

public enum ProblematicNameProblematC {
  @SerializedName("1")
  _1("1"),

  @SerializedName("two-point-zero")
  TWO_POINT_ZERO("two-point-zero"),

  @SerializedName("three?")
  THREE("three?");

  private final String serializedName;

  ProblematicNameProblematC(String serializedName) {
    this.serializedName = serializedName;
  }

  @Override
  public String toString() {
    return serializedName;
  }
}
