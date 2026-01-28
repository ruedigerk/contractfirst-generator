package jsr305_server_spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProblematicNameProblematC {
  @JsonProperty("1")
  _1("1"),

  @JsonProperty("two-point-zero")
  TWO_POINT_ZERO("two-point-zero"),

  @JsonProperty("three?")
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
