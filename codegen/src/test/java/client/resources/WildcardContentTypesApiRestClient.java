package client.resources;

import client.model.Error;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.RestClientIoException;
import de.rk42.openapi.codegen.client.RestClientSupport;
import de.rk42.openapi.codegen.client.RestClientValidationException;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.ParameterLocation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
import java.io.InputStream;

public class WildcardContentTypesApiRestClient {
  private final RestClientSupport support;

  public WildcardContentTypesApiRestClient(RestClientSupport support) {
    this.support = support;
  }

  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse getWildcardContentTypesWithResponse(String testCaseSelector) throws
      RestClientIoException, RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/wildcardContentTypes", "GET");

    builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

    builder.response(StatusCode.of(200), "text/*", String.class);
    builder.response(StatusCode.of(200), "application/*", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
