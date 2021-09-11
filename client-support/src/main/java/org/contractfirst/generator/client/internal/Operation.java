package org.contractfirst.generator.client.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents the definition of an API operation and the data that is transferred within it.
 */
public class Operation {

  private final String path;
  private final String method;
  private final List<Parameter> parameters;
  private final OperationRequestBody requestBody;
  private final Map<StatusCode, List<ResponseDefinition>> responseDefinitions;

  private Operation(Builder builder) {
    this.path = builder.path;
    this.method = builder.method;
    this.parameters = new ArrayList<>(builder.parameters);
    this.requestBody = builder.requestBody;
    this.responseDefinitions = builder.responseDefinitions.stream().collect(Collectors.groupingBy(ResponseDefinition::getStatusCode));
  }

  public List<ResponseDefinition> selectResponseDefinitionsByStatusCode(int statusCode) {
    List<ResponseDefinition> responses = responseDefinitions.get(StatusCode.of(statusCode));

    if (responses != null) {
      return responses;
    } else {
      return Optional.ofNullable(responseDefinitions.get(StatusCode.DEFAULT)).orElse(Collections.emptyList());
    }
  }

  public Map<String, Parameter> getPathParameters() {
    return extractParameters(ParameterLocation.PATH, parameters);
  }

  public Map<String, Parameter> getQueryParameters() {
    return extractParameters(ParameterLocation.QUERY, parameters);
  }

  public Map<String, Parameter> getHeaderParameters() {
    return extractParameters(ParameterLocation.HEADER, parameters);
  }

  private Map<String, Parameter> extractParameters(ParameterLocation location, List<Parameter> parameters) {
    return parameters.stream()
        .filter(param -> param.getLocation() == location)
        .collect(Collectors.toMap(Parameter::getName, Function.identity()));
  }

  public List<String> getAllAcceptedMediaTypes() {
    return responseDefinitions.values()
        .stream()
        .flatMap(list -> list.stream().map(ResponseDefinition::getContentType))
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  public String getPath() {
    return path;
  }

  public String getMethod() {
    return method;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public OperationRequestBody getRequestBody() {
    return requestBody;
  }

  public Map<StatusCode, List<ResponseDefinition>> getResponseDefinitions() {
    return responseDefinitions;
  }

  /**
   * Builder for instances of class Operation.
   */
  public static class Builder {

    private final List<Parameter> parameters = new ArrayList<>();
    private final List<ResponseDefinition> responseDefinitions = new ArrayList<>();

    private final String path;
    private final String method;

    // Default is "no body", indicated by contentType null
    private OperationRequestBody requestBody = new OperationRequestBody(null, false, null);

    public Builder(String path, String method) {
      this.path = path;
      this.method = method;
    }

    /**
     * Adds a parameter definition to this operation.
     */
    public void parameter(String name, ParameterLocation location, boolean required, Object value) {
      parameters.add(new Parameter(name, location, required, value));
    }

    /**
     * Defines the request body for this operation.
     */
    public void requestBody(String contentType, boolean required, Object entity) {
      requestBody = new OperationRequestBody(contentType, required, entity);
    }

    /**
     * Adds a response definition without content/body.
     */
    public void response(StatusCode statusCode) {
      responseDefinitions.add(new ResponseDefinition(statusCode, null, null));
    }

    /**
     * Adds a response definition with content of the specified content type and Java type.
     */
    public void response(StatusCode statusCode, String contentType, Type javaType) {
      responseDefinitions.add(new ResponseDefinition(statusCode, contentType, javaType));
    }

    /**
     * Build an operation from this builder.
     */
    public Operation build() {
      return new Operation(this);
    }
  }
}
