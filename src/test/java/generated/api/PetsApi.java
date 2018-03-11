package generated.api;

import generated.api.support.*;
import generated.model.Error;
import generated.model.*;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/pets")
public interface PetsApi {

  @POST
  @Produces({"application/json"})
  CreatePetsResponse createPets();

  class CreatePetsResponse extends ResponseWrapper {

    private CreatePetsResponse(Response delegate) {
      super(delegate);
    }

    public static CreatePetsResponse with201() {
      return new CreatePetsResponse(Response.status(201).build());
    }

    public static CreatePetsResponse with200ApplicationJson(Error entity) {
      return new CreatePetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreatePetsResponse withCustomResponse(Response response) {
      return new CreatePetsResponse(response);
    }
  }

  @GET
  @Produces({"application/json"})
  ListPetsResponse listPets(@QueryParam("limit") Integer limit);

  class ListPetsResponse extends ResponseWrapper {

    private ListPetsResponse(Response delegate) {
      super(delegate);
    }

    public static ListPetsResponse with200ApplicationJson(List<Pet> entity) {
      return new ListPetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListPetsResponse with200ApplicationJson(Error entity) {
      return new ListPetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListPetsResponse withCustomResponse(Response response) {
      return new ListPetsResponse(response);
    }
  }

  @GET
  @Path("/{petId}")
  @Produces({"application/json"})
  ShowPetByIdResponse showPetById(@PathParam("petId") String petId);

  class ShowPetByIdResponse extends ResponseWrapper {

    private ShowPetByIdResponse(Response delegate) {
      super(delegate);
    }

    public static ShowPetByIdResponse with200ApplicationJson(List<Pet> entity) {
      return new ShowPetByIdResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ShowPetByIdResponse with200ApplicationJson(Error entity) {
      return new ShowPetByIdResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ShowPetByIdResponse withCustomResponse(Response response) {
      return new ShowPetByIdResponse(response);
    }
  }
}
