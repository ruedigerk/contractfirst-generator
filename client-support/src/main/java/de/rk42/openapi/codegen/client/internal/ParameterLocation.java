package de.rk42.openapi.codegen.client.internal;

/**
 * Represents the possible locations a parameter of an API operation can have.
 */
public enum ParameterLocation {

  QUERY,
  HEADER,
  PATH,
  COOKIE
}
