package de.rk42.openapi.codegen.client;

import java.lang.reflect.Type;

/**
 * Builder for constructing Responses, either defined or undefined.
 */
class ResponseBuilder {

  private final CorrespondingRequest request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;

  public ResponseBuilder(CorrespondingRequest request, int statusCode, String httpStatusMessage, String contentType) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
  }

  public UndefinedResponse unexpectedResponse(String responseContent, String reason) {
    return new UndefinedResponse(request, statusCode, httpStatusMessage, contentType, responseContent, reason, null);
  }

  public UndefinedResponse unexpectedResponse(String responseContent, String reason, Throwable cause) {
    return new UndefinedResponse(request, statusCode, httpStatusMessage, contentType, responseContent, reason, cause);
  }

  public DefinedResponse expectedResponse(Type javaType, Object entity) {
    return new DefinedResponse(request, statusCode, httpStatusMessage, contentType, javaType, entity);
  }

  public IncompleteResponse incompleteResponse() {
    return new IncompleteResponse(request, statusCode, httpStatusMessage, contentType);
  }
}
