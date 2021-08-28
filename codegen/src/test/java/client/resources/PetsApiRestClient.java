package client.resources;

import client.model.Error;
import client.model.Manual;
import client.model.Pet;
import com.google.gson.reflect.TypeToken;
import de.rk42.openapi.codegen.client.DefinedResponse;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.RestClientIoException;
import de.rk42.openapi.codegen.client.RestClientSupport;
import de.rk42.openapi.codegen.client.RestClientUndefinedResponseException;
import de.rk42.openapi.codegen.client.RestClientValidationException;
import de.rk42.openapi.codegen.client.model.Operation;
import de.rk42.openapi.codegen.client.model.ParameterLocation;
import de.rk42.openapi.codegen.client.model.StatusCode;
import java.io.InputStream;
import java.util.List;

public class PetsApiRestClient {
  private final RestClientSupport support;

  public PetsApiRestClient(RestClientSupport support) {
    this.support = support;
  }

  /**
   * Test for 204 response.
   */
  public void changePet(Pet requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = changePetWithResponse(requestBody);
    DefinedResponse response = genericResponse.asExpectedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }
  }

  /**
   * Test for 204 response.
   */
  public GenericResponse changePetWithResponse(Pet requestBody) throws RestClientIoException,
      RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/pets", "PUT");

    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test operation for generating generic types, e.g. List of Pet.
   */
  public List<Pet> filterPets(List<Pet> requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = filterPetsWithResponse(requestBody);
    DefinedResponse response = genericResponse.asExpectedResponse();

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
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param petStoreId ID of pet store
   * @param dryRun Do a dry run?
   * @param customerId Optional customer ID
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public Pet createPet(String petStoreId, Boolean dryRun, Long customerId, String testCaseSelector,
      Pet requestBody) throws RestClientIoException, RestClientValidationException,
      RestClientUndefinedResponseException {

    GenericResponse genericResponse = createPetWithResponse(petStoreId, dryRun, customerId, testCaseSelector, requestBody);
    DefinedResponse response = genericResponse.asExpectedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }

    return (Pet) response.getEntity();
  }

  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param petStoreId ID of pet store
   * @param dryRun Do a dry run?
   * @param customerId Optional customer ID
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse createPetWithResponse(String petStoreId, Boolean dryRun, Long customerId,
      String testCaseSelector, Pet requestBody) throws RestClientIoException,
      RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/{petStoreId}/pets", "POST");

    builder.parameter("petStoreId", ParameterLocation.PATH, true, petStoreId);
    builder.parameter("dryRun", ParameterLocation.QUERY, false, dryRun);
    builder.parameter("customerId", ParameterLocation.HEADER, false, customerId);
    builder.parameter("testCaseSelector", ParameterLocation.QUERY, false, testCaseSelector);
    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", Pet.class);
    builder.response(StatusCode.of(400), "application/json", Error.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test case for multiple response content types with different schemas.
   */
  public GenericResponse getManualWithResponse() throws RestClientIoException,
      RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/manuals", "GET");

    builder.response(StatusCode.of(200), "application/json", Manual.class);
    builder.response(StatusCode.of(200), "application/pdf", InputStream.class);
    builder.response(StatusCode.of(202), "application/json", Pet.class);
    builder.response(StatusCode.of(204));
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test binary input and output.
   */
  public InputStream uploadAndReturnBinary(InputStream requestBody) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = uploadAndReturnBinaryWithResponse(requestBody);
    DefinedResponse response = genericResponse.asExpectedResponse();

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

    Operation.Builder builder = new Operation.Builder("/manuals", "PUT");

    builder.requestBody("application/octet-stream", true, requestBody);

    builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }

  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GenericResponse postManualWithResponse(String testCaseSelector) throws
      RestClientIoException, RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/manuals", "POST");

    builder.parameter("testCaseSelector", ParameterLocation.QUERY, false, testCaseSelector);

    builder.response(StatusCode.of(200), "application/json", Manual.class);
    builder.response(StatusCode.of(200), "application/*", InputStream.class);
    builder.response(StatusCode.of(200), "image/*", InputStream.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
