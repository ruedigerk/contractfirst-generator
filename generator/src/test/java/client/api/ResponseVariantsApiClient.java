package client.api;

import client.model.Failure;
import client.model.Item;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientUndefinedResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.DefinedResponse;
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;

public class ResponseVariantsApiClient {
  private final ApiClientSupport support;

  public ResponseVariantsApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param systemId ID of the system to create the item in.
   * @param dryRun Do a dry run?
   * @param partNumber Optional part number
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public Item createItem(String systemId, Boolean dryRun, Long partNumber, String testCaseSelector,
      Item requestBody) throws ApiClientIoException, ApiClientValidationException,
      ApiClientUndefinedResponseException {

    GenericResponse genericResponse = createItemWithResponse(systemId, dryRun, partNumber, testCaseSelector, requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }

    return (Item) response.getEntity();
  }

  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param systemId ID of the system to create the item in.
   * @param dryRun Do a dry run?
   * @param partNumber Optional part number
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse createItemWithResponse(String systemId, Boolean dryRun, Long partNumber,
      String testCaseSelector, Item requestBody) throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/{systemId}/components", "POST");

    builder.parameter("systemId", ParameterLocation.PATH, true, systemId);
    builder.parameter("dryRun", ParameterLocation.QUERY, false, dryRun);
    builder.parameter("partNumber", ParameterLocation.HEADER, false, partNumber);
    builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);
    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", Item.class);
    builder.response(StatusCode.of(201));
    builder.response(StatusCode.of(204));
    builder.response(StatusCode.of(400), "application/json", Failure.class);
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
