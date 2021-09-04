package client.api;

import client.model.Error;
import client.model.Manual;
import de.rk42.openapi.codegen.client.ApiClientIoException;
import de.rk42.openapi.codegen.client.ApiClientSupport;
import de.rk42.openapi.codegen.client.ApiClientValidationException;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.ParameterLocation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
import java.io.InputStream;

public class MultipleContentTypesApiClient {
  private final ApiClientSupport support;

  public MultipleContentTypesApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * Test case for multiple response content types with different schemas.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse getManualWithResponse(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/manuals", "GET");

    builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

    builder.response(StatusCode.of(200), "application/json", Manual.class);
    builder.response(StatusCode.of(200), "application/pdf", InputStream.class);
    builder.response(StatusCode.of(202), "text/plain", String.class);
    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
