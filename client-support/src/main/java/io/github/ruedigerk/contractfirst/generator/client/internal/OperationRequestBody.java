package io.github.ruedigerk.contractfirst.generator.client.internal;

/**
 * Represents the definition of the request body of an API operation, and the entity that is transferred in it.
 */
public class OperationRequestBody {

  private final String contentType;
  private final boolean required;
  private final Object entity;

  public OperationRequestBody(String contentType, boolean required, Object entity) {
    this.contentType = contentType;
    this.required = required;
    this.entity = entity;
  }

  public String getContentType() {
    return contentType;
  }

  public boolean isRequired() {
    return required;
  }

  public Object getEntity() {
    return entity;
  }
}
