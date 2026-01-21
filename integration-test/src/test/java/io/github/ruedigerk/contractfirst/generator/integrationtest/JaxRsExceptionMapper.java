package io.github.ruedigerk.contractfirst.generator.integrationtest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * JAX-RS Exception mapper for logging exceptions thrown during request processing of the embedded JAX-RS server used in the tests.
 */
@Provider
public class JaxRsExceptionMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable throwable) {
    System.out.println("SERVER: Exception " + throwable);
    throwable.printStackTrace(System.out);

    if (throwable instanceof WebApplicationException) {
      return ((WebApplicationException) throwable).getResponse();
    }

    return Response.serverError().build();
  }
}
