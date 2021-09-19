package client.api;

import client.model.Failure;
import client.model.Item;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.util.List;
import org.contractfirst.generator.client.ApiClientIoException;
import org.contractfirst.generator.client.ApiClientSupport;
import org.contractfirst.generator.client.ApiClientUndefinedResponseException;
import org.contractfirst.generator.client.ApiClientValidationException;
import org.contractfirst.generator.client.DefinedResponse;
import org.contractfirst.generator.client.GenericResponse;
import org.contractfirst.generator.client.internal.Operation;
import org.contractfirst.generator.client.internal.StatusCode;

public class PayloadVariantsApiClient {
  private final ApiClientSupport support;

  public PayloadVariantsApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * Test operation for generating generic types, e.g. List of Item.
   */
  public List<Item> filterItems(List<Item> requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = filterItemsWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }

    return (List<Item>) response.getEntity();
  }

  /**
   * Test operation for generating generic types, e.g. List of Item.
   */
  public GenericResponse filterItemsWithResponse(List<Item> requestBody) throws
      ApiClientIoException, ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/items", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", new TypeToken<List<Item>>(){}.getType());
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test binary input and output.
   */
  public InputStream uploadAndReturnBinary(InputStream requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = uploadAndReturnBinaryWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }

    return (InputStream) response.getEntity();
  }

  /**
   * Test binary input and output.
   */
  public GenericResponse uploadAndReturnBinaryWithResponse(InputStream requestBody) throws
      ApiClientIoException, ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/itemBinaries", "PUT");

    builder.requestBody("application/octet-stream", true, requestBody);

    builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test for 204 response.
   */
  public void changeItem(Item requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = changeItemWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }
  }

  /**
   * Test for 204 response.
   */
  public GenericResponse changeItemWithResponse(Item requestBody) throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/itemBinaries", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
