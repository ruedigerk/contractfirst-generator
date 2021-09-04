package de.rk42.openapi.codegen.client;

import java.util.List;

/**
 * Describes a request sent by the API client.
 */
public class RequestDescription {

  private final String url;
  private final String method;
  private final List<Header> headers;

  public RequestDescription(String url, String method, List<Header> headers) {
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
