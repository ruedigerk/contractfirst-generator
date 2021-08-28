package de.rk42.openapi.codegen.client;

/**
 * Abstract superclass of all exceptions that indicate an error defined in the contract.
 */
public abstract class RestClientDefinedErrorException extends RestClientException {

  private final int httpStatusCode;
  private final Object entity;

  protected RestClientDefinedErrorException(int httpStatusCode, Object entity) {
    super(toMessage(httpStatusCode, entity));
    this.httpStatusCode = httpStatusCode;
    this.entity = entity;
  }

  private static String toMessage(int httpStatusCode, Object entity) {
    if (entity == null) {
      return "httpStatusCode=" + httpStatusCode;
    } else {
      return "httpStatusCode=" + httpStatusCode + ", entity=" + entity;
    }
  }

  public int getHttpStatusCode() {
    return httpStatusCode;
  }

  public Object getEntity() {
    return entity;
  }
}
