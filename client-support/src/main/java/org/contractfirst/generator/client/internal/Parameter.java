package org.contractfirst.generator.client.internal;

/**
 * Represents the definition of a parameter of an API operation and the value that is transferred in it. The combination of name and location must be
 * unique for all parameters of an API operation.
 */
public class Parameter {

  private final String name;
  private final ParameterLocation location;
  private final boolean required;
  private final Object value;

  public Parameter(String name, ParameterLocation location, boolean required, Object value) {
    this.name = name;
    this.location = location;
    this.required = required;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public ParameterLocation getLocation() {
    return location;
  }

  public boolean isRequired() {
    return required;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return name + " (" + location + ")";
  }
}
