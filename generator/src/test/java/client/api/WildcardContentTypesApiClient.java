package client.api;

import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.io.InputStream;

public class WildcardContentTypesApiClient {
  private final ApiClientSupport support;

  public WildcardContentTypesApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse getWildcardContentTypesWithResponse(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/wildcardContentTypes", "GET");

    builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

    builder.response(StatusCode.of(200), "text/*", String.class);
    builder.response(StatusCode.of(200), "application/*", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
