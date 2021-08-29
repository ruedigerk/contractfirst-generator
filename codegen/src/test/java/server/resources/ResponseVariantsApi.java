package server.resources;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import server.model.Error;
import server.model.Pet;
import server.resources.support.ResponseWrapper;

@Path("")
public interface ResponseVariantsApi {
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
      @HeaderParam("testCaseSelector") String testCaseSelector, @NotNull @Valid Pet requestBody);

  class CreatePetResponse extends ResponseWrapper {
    private CreatePetResponse(Response delegate) {
      super(delegate);
    }

    public static CreatePetResponse with200ApplicationJson(Pet entity) {
      return new CreatePetResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreatePetResponse with201() {
      return new CreatePetResponse(Response.status(201).build());
    }

    public static CreatePetResponse with204() {
      return new CreatePetResponse(Response.status(204).build());
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
}
