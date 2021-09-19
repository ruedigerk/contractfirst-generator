package client.api;

import client.model.Error;
import java.io.InputStream;
import org.contractfirst.generator.client.ApiClientIoException;
import org.contractfirst.generator.client.ApiClientSupport;
import org.contractfirst.generator.client.ApiClientValidationException;
import org.contractfirst.generator.client.GenericResponse;
import org.contractfirst.generator.client.internal.Operation;
import org.contractfirst.generator.client.internal.ParameterLocation;
import org.contractfirst.generator.client.internal.StatusCode;

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
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}