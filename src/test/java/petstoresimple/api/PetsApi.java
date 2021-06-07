package petstoresimple.api;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import petstoresimple.api.support.ResponseWrapper;
import petstoresimple.model.Pet;

@Path("")
public interface PetsApi {
  @GET
  @Path("/pets")
  @Produces({
      "application/json",
      "application/xml"
  })
  ListPetsResponse listPets(@QueryParam("limit") Integer limit);

  @POST
  @Path("/pets")
  @Produces("application/json")
  CreatePetsResponse createPets();

  @GET
  @Path("/pets/{petId}")
  @Produces("application/json")
  ShowPetByIdResponse showPetById(@PathParam("petId") String petId);

  class ListPetsResponse extends ResponseWrapper {
    private ListPetsResponse(Response delegate) {
      super(delegate);
    }

    public static ListPetsResponse with200ApplicationJson(List<Pet> entity) {
      return new ListPetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListPetsResponse with200ApplicationXml(List<Pet> entity) {
      return new ListPetsResponse(Response.status(200).header("Content-Type", "application/xml").entity(entity).build());
    }

    public static ListPetsResponse withCustomResponse(Response response) {
      return new ListPetsResponse(response);
    }
  }

  class CreatePetsResponse extends ResponseWrapper {
    private CreatePetsResponse(Response delegate) {
      super(delegate);
    }

    public static CreatePetsResponse with201() {
      return new CreatePetsResponse(Response.status(201).build());
    }

    public static CreatePetsResponse withCustomResponse(Response response) {
      return new CreatePetsResponse(response);
    }
  }

  class ShowPetByIdResponse extends ResponseWrapper {
    private ShowPetByIdResponse(Response delegate) {
      super(delegate);
    }

    public static ShowPetByIdResponse with200ApplicationJson(List<Pet> entity) {
      return new ShowPetByIdResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ShowPetByIdResponse withCustomResponse(Response response) {
      return new ShowPetByIdResponse(response);
    }
  }
}