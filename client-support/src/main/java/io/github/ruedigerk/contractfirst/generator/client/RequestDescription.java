package io.github.ruedigerk.contractfirst.generator.client;

import java.util.List;
import java.util.Objects;

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

  /**
   * The URL of the request.
   */
  public String getUrl() {
    return url;
  }

  /**
   * The HTTP method of the request, e.g., "GET".
   */
  public String getMethod() {
    return method;
  }

  /**
   * The headers of the HTTP request.
   */
  public List<Header> getHeaders() {
    return headers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestDescription that = (RequestDescription) o;
    return Objects.equals(url, that.url) && Objects.equals(method, that.method) && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, method, headers);
  }

  @Override
  public String toString() {
    return "RequestDescription(" + method + " " + url + ",headers=" + headers + ')';
  }
}
