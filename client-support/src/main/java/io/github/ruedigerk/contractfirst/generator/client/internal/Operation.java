package io.github.ruedigerk.contractfirst.generator.client.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okhttp3.MediaType;

/**
 * Represents the definition of an API operation, and the data that is transferred within it.
 */
public class Operation {

  private final String path;
  private final String method;
  private final List<Parameter> parameters;
  private final OperationRequestBody requestBody;
  private final Map<StatusCode, List<ResponseDefinition>> responseDefinitions;

  private Operation(Builder builder) {
    path = builder.path;
    method = builder.method;
    parameters = new ArrayList<>(builder.parameters);
    requestBody = builder.requestBody;
    responseDefinitions = builder.responseDefinitions.stream().collect(Collectors.groupingBy(ResponseDefinition::getStatusCode));
  }

  /**
   * Determines the accept-header value for this operation. All JSON-compatible mime types are sent with a q-factor of 1 and all other mime types with a
   * q-factor of 0.5.
   */
  // See: https://developer.mozilla.org/en-US/docs/Glossary/Quality_values
  // See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept
  // See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Content_negotiation
  public String determineAcceptHeaderValue() {
    Map<Boolean, List<String>> partitionedMediaTypes = getAllAcceptedMediaTypes()
        .stream()
        .collect(Collectors.partitioningBy(MediaTypes::isJsonMediaType));

    List<String> jsonMediaTypes = partitionedMediaTypes.get(true);
    List<String> nonJsonMediaTypes = partitionedMediaTypes.get(false);

    return Stream.concat(
        jsonMediaTypes.stream(),
        nonJsonMediaTypes.stream().map(mediaType -> mediaType + "; q=0.5")
    ).collect(Collectors.joining(", "));
  }

  private List<String> getAllAcceptedMediaTypes() {
    return responseDefinitions.values()
        .stream()
        .flatMap(list -> list.stream().map(ResponseDefinition::getContentType))
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * Returns the Java type of the response definition that matches the servers returned status code and content type. If no response definition is matching,
   * null is returned. If the matching response is defined to have no content, type {@link Void#TYPE} is returned.
   */
  public Type determineMatchingResponseType(int statusCode, String contentType) {
    List<ResponseDefinition> responseDefinitions = selectResponseDefinitionsByStatusCode(statusCode);

    if (responseDefinitions.stream().allMatch(ResponseDefinition::hasNoContent)) {
      return Void.TYPE;
    }

    if (contentType == null) {
      if (responseDefinitions.stream().anyMatch(ResponseDefinition::hasNoContent)) {
        return Void.TYPE;
      } else {
        return null;
      }
    }

    MediaType mediaType = MediaTypes.parseNullable(contentType);

    for (ResponseDefinition definition : responseDefinitions) {
      if (definition.getContentType() != null && isCompatibleMediaType(mediaType, MediaTypes.parseNullable(definition.getContentType()))) {
        return definition.getJavaType();
      }
    }

    // No matching definition found. As a special case, if the server sends a JSON content type and there is only a single response definition for this status
    // code in the contract, try to deserialize the response as a JSON entity. This quirk is added, because there seem to be a lot of contracts in the wild that
    // erroneously declare some none-JSON content type in the contract but actually send JSON encoded response entities.
    if (MediaTypes.isJsonMediaType(mediaType) && responseDefinitions.size() == 1) {
      return responseDefinitions.get(0).getJavaType();
    }

    return null;
  }

  private List<ResponseDefinition> selectResponseDefinitionsByStatusCode(int statusCode) {
    List<ResponseDefinition> responses = responseDefinitions.get(StatusCode.of(statusCode));

    if (responses != null) {
      return responses;
    } else {
      return Optional.ofNullable(responseDefinitions.get(StatusCode.DEFAULT)).orElse(Collections.emptyList());
    }
  }

  private boolean isCompatibleMediaType(MediaType testedMediaType, MediaType mediaTypeToMatchAgainst) {
    if (mediaTypeToMatchAgainst == null) {
      return false;
    }
    if (mediaTypeToMatchAgainst.type().equals("*")) {
      return true;
    }
    if (testedMediaType == null) {
      return false;
    }

    boolean sameType = mediaTypeToMatchAgainst.type().equals(testedMediaType.type());
    return sameType && (mediaTypeToMatchAgainst.subtype().equals("*") || mediaTypeToMatchAgainst.subtype().equals(testedMediaType.subtype()));
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

  /**
   * Builder for instances of class Operation.
   */
  public static class Builder {

    private final List<Parameter> parameters = new ArrayList<>();
    private final List<ResponseDefinition> responseDefinitions = new ArrayList<>();
    private final List<BodyPart> bodyParts = new ArrayList<>();

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
     * Defines a form field or part of the request body for this operation.
     */
    public void requestBodyPart(String name, Object value) {
      bodyParts.add(new BodyPart(name, value));
    }

    /**
     * Defines the request body as a multipart, or an application/x-www-form-urlencoded body.
     */
    public void multipartRequestBody(String contentType) {
      requestBody = new OperationRequestBody(contentType, false, new MultipartRequestBody(bodyParts));
    }

    /**
     * Adds a response definition without content/body.
     */
    public void response(StatusCode statusCode) {
      responseDefinitions.add(new ResponseDefinition(statusCode, null, Void.TYPE));
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
