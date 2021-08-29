package client.resources;

import client.model.Error;
import client.model.Pet;
import com.google.gson.reflect.TypeToken;
import de.rk42.openapi.codegen.client.DefinedResponse;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.RestClientIoException;
import de.rk42.openapi.codegen.client.RestClientSupport;
import de.rk42.openapi.codegen.client.RestClientUndefinedResponseException;
import de.rk42.openapi.codegen.client.RestClientValidationException;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
import java.io.InputStream;
import java.util.List;

public class PayloadVariantsApiRestClient {
  private final RestClientSupport support;

  public PayloadVariantsApiRestClient(RestClientSupport support) {
    this.support = support;
  }

  /**
   * Test operation for generating generic types, e.g. List of Pet.
   */
  public List<Pet> filterPets(List<Pet> requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

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
  public GenericResponse filterPetsWithResponse(List<Pet> requestBody) throws RestClientIoException,
      RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/pets", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", new TypeToken<List<Pet>>(){}.getType());
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test binary input and output.
   */
  public InputStream uploadAndReturnBinary(InputStream requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

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
      RestClientIoException, RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/petBinaries", "PUT");

    builder.requestBody("application/octet-stream", true, requestBody);

    builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test for 204 response.
   */
  public void changePet(Pet requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = changePetWithResponse(requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }
  }

  /**
   * Test for 204 response.
   */
  public GenericResponse changePetWithResponse(Pet requestBody) throws RestClientIoException,
      RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/petBinaries", "POST");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
