package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Abstract superclass of all exceptions that indicate an error defined in the contract. Assumes that the error is completely described by the
 * status code and the returned error entity.
 */
public abstract class ApiClientEntityException extends ApiClientException {

  private final int httpStatusCode;
  private final Object entity;

  protected ApiClientEntityException(int httpStatusCode, Object entity) {
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
