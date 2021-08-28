package server.resources;

import java.io.InputStream;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import server.model.Error;
import server.model.Manual;
import server.model.Pet;
import server.resources.support.ResponseWrapper;

@Path("")
public interface PetsApi {
  /**
   * Test for 204 response.
   */
  @PUT
  @Path("/pets")
  @Consumes("application/json")
  @Produces("application/json")
  ChangePetResponse changePet(@NotNull @Valid Pet requestBody);

  /**
   * Test operation for generating generic types, e.g. List of Pet.
   */
  @POST
  @Path("/pets")
  @Consumes("application/json")
  @Produces("application/json")
  FilterPetsResponse filterPets(@NotNull @Valid List<Pet> requestBody);

  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param petStoreId ID of pet store
   * @param dryRun Do a dry run?
   * @param customerId Optional customer ID
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @POST
  @Path("/{petStoreId}/pets")
  @Consumes("application/json")
  @Produces("application/json")
  CreatePetResponse createPet(@PathParam("petStoreId") @NotNull String petStoreId,
      @QueryParam("dryRun") Boolean dryRun, @HeaderParam("customerId") Long customerId,
      @QueryParam("testCaseSelector") String testCaseSelector, @NotNull @Valid Pet requestBody);

  /**
   * Test case for multiple response content types with different schemas.
   */
  @GET
  @Path("/manuals")
  @Produces({
      "application/json",
      "application/pdf"
  })
  GetManualResponse getManual();

  /**
   * Test binary input and output.
   */
  @PUT
  @Path("/manuals")
  @Consumes("application/octet-stream")
  @Produces({
      "application/json",
      "application/octet-stream"
  })
  UploadAndReturnBinaryResponse uploadAndReturnBinary(@NotNull InputStream requestBody);

  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @POST
  @Path("/manuals")
  @Produces({
      "application/*",
      "application/json",
      "image/*"
  })
  PostManualResponse postManual(@QueryParam("testCaseSelector") String testCaseSelector);

  class ChangePetResponse extends ResponseWrapper {
    private ChangePetResponse(Response delegate) {
      super(delegate);
    }

    public static ChangePetResponse with204() {
      return new ChangePetResponse(Response.status(204).build());
    }

    public static ChangePetResponse withApplicationJson(int status, Error entity) {
      return new ChangePetResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ChangePetResponse withCustomResponse(Response response) {
      return new ChangePetResponse(response);
    }
  }

  class FilterPetsResponse extends ResponseWrapper {
    private FilterPetsResponse(Response delegate) {
      super(delegate);
    }

    public static FilterPetsResponse with200ApplicationJson(List<Pet> entity) {
      return new FilterPetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterPetsResponse withApplicationJson(int status, Error entity) {
      return new FilterPetsResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterPetsResponse withCustomResponse(Response response) {
      return new FilterPetsResponse(response);
    }
  }

  class CreatePetResponse extends ResponseWrapper {
    private CreatePetResponse(Response delegate) {
      super(delegate);
    }

    public static CreatePetResponse with200ApplicationJson(Pet entity) {
      return new CreatePetResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreatePetResponse with400ApplicationJson(Error entity) {
      return new CreatePetResponse(Response.status(400).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreatePetResponse withApplicationJson(int status, Error entity) {
      return new CreatePetResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreatePetResponse withCustomResponse(Response response) {
      return new CreatePetResponse(response);
    }
  }

  class GetManualResponse extends ResponseWrapper {
    private GetManualResponse(Response delegate) {
      super(delegate);
    }

    public static GetManualResponse with200ApplicationJson(Manual entity) {
      return new GetManualResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetManualResponse with200ApplicationPdf(InputStream entity) {
      return new GetManualResponse(Response.status(200).header("Content-Type", "application/pdf").entity(entity).build());
    }

    public static GetManualResponse with202ApplicationJson(Pet entity) {
      return new GetManualResponse(Response.status(202).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetManualResponse with204() {
      return new GetManualResponse(Response.status(204).build());
    }

    public static GetManualResponse withApplicationJson(int status, Error entity) {
      return new GetManualResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetManualResponse withCustomResponse(Response response) {
      return new GetManualResponse(response);
    }
  }

  class UploadAndReturnBinaryResponse extends ResponseWrapper {
    private UploadAndReturnBinaryResponse(Response delegate) {
      super(delegate);
    }

    public static UploadAndReturnBinaryResponse with200ApplicationOctetStream(InputStream entity) {
      return new UploadAndReturnBinaryResponse(Response.status(200).header("Content-Type", "application/octet-stream").entity(entity).build());
    }

    public static UploadAndReturnBinaryResponse withApplicationJson(int status, Error entity) {
      return new UploadAndReturnBinaryResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static UploadAndReturnBinaryResponse withCustomResponse(Response response) {
      return new UploadAndReturnBinaryResponse(response);
    }
  }

  class PostManualResponse extends ResponseWrapper {
    private PostManualResponse(Response delegate) {
      super(delegate);
    }

    public static PostManualResponse with200ApplicationJson(Manual entity) {
      return new PostManualResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static PostManualResponse with200ApplicationStar(InputStream entity) {
      return new PostManualResponse(Response.status(200).header("Content-Type", "application/*").entity(entity).build());
    }

    public static PostManualResponse with200ImageStar(InputStream entity) {
      return new PostManualResponse(Response.status(200).header("Content-Type", "image/*").entity(entity).build());
    }

    public static PostManualResponse withApplicationJson(int status, Error entity) {
      return new PostManualResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static PostManualResponse withCustomResponse(Response response) {
      return new PostManualResponse(response);
    }
  }
}
