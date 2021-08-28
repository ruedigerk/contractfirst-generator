package de.rk42.openapi.codegen.client;

import java.util.List;

/**
 * Represents the request corresponding to a response returned by the client or an exception thrown by it.
 */
public class CorrespondingRequest {

  private final String url;
  private final String method;
  private final List<Header> headers;

  public CorrespondingRequest(String url, String method, List<Header> headers) {
    this.url = url;
    this.method = method;
    this.headers = headers;
  }

  public String getUrl() {
    return url;
  }

  public String getMethod() {
    return method;
  }

  public List<Header> getHeaders() {
    return headers;
  }

  @Override
  public String toString() {
    return method + " " + url;
  }
}
