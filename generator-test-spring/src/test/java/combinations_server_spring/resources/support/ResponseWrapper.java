package combinations_server_spring.resources.support;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * Response wrapper for typesafe responses.
 */
public abstract class ResponseWrapper extends ResponseEntity<Object> {

  private final ResponseEntity<?> delegate;

  protected ResponseWrapper(ResponseEntity<?> delegate) {
    super(HttpStatus.OK);
    this.delegate = delegate;
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public HttpStatusCode getStatusCode() {
    return delegate.getStatusCode();
  }

  @Override
  public HttpHeaders getHeaders() {
    return delegate.getHeaders();
  }

  @Override
  public @Nullable Object getBody() {
    return delegate.getBody();
  }

  @Override
  public boolean hasBody() {
    return delegate.hasBody();
  }
}
