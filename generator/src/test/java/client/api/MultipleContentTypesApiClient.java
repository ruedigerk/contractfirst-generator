package client.api;

import client.model.Failure;
import client.model.Manual;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
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
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
