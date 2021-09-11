package client.api;

import client.model.Error;
import client.model.Pet;
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
   * Test operation for generating generic types, e.g. List of Pet.
   */
  public List<Pet> filterPets(List<Pet> requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = filterPetsWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }

    return (List<Pet>) response.getEntity();
  }

  /**
   * Test operation for generating generic types, e.g. List of Pet.
   */
  public GenericResponse filterPetsWithResponse(List<Pet> requestBody) throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/pets", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", new TypeToken<List<Pet>>(){}.getType());
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

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
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }

    return (InputStream) response.getEntity();
  }

  /**
   * Test binary input and output.
   */
  public GenericResponse uploadAndReturnBinaryWithResponse(InputStream requestBody) throws
      ApiClientIoException, ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/petBinaries", "PUT");

    builder.requestBody("application/octet-stream", true, requestBody);

    builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test for 204 response.
   */
  public void changePet(Pet requestBody) throws ApiClientIoException, ApiClientValidationException,
      ApiClientUndefinedResponseException {

    GenericResponse genericResponse = changePetWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }
  }

  /**
   * Test for 204 response.
   */
  public GenericResponse changePetWithResponse(Pet requestBody) throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/petBinaries", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
