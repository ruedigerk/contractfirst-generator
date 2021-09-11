package server.resources;

import java.io.InputStream;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server.model.Error;
import server.model.Pet;
import server.resources.support.ResponseWrapper;

@Path("")
public interface PayloadVariantsApi {
  /**
   * Test operation for generating generic types, e.g. List of Pet.
   */
  @POST
  @Path("/pets")
  @Consumes("application/json")
  @Produces("application/json")
  FilterPetsResponse filterPets(@NotNull @Valid List<Pet> requestBody);

  /**
   * Test binary input and output.
   */
  @PUT
  @Path("/petBinaries")
  @Consumes("application/octet-stream")
  @Produces({
      "application/json",
      "application/octet-stream"
  })
  UploadAndReturnBinaryResponse uploadAndReturnBinary(@NotNull InputStream requestBody);

  /**
   * Test for 204 response.
   */
  @POST
  @Path("/petBinaries")
  @Consumes("application/json")
  @Produces("application/json")
  ChangePetResponse changePet(@NotNull @Valid Pet requestBody);

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
}
