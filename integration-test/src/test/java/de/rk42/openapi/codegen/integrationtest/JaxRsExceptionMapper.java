package de.rk42.openapi.codegen.integrationtest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * JAX-RS Exception mapper for logging exceptions thrown during request processing of the embedded JAX-RS server used in the tests.
 */
@Provider
public class JaxRsExceptionMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable throwable) {
    System.out.println("SERVER: Exception " + throwable);

    if (throwable instanceof WebApplicationException) {
      return ((WebApplicationException) throwable).getResponse();
    }

    return Response.serverError().build();
  }
}
