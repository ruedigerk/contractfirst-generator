package server.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import server.model.Failure;
import server.model.Item;
import server.resources.support.ResponseWrapper;

@Path("")
public interface ResponseVariantsApi {
  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param systemId ID of the system to create the item in.
   * @param dryRun Do a dry run?
   * @param partNumber Optional part number
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @POST
  @Path("/{systemId}/components")
  @Consumes("application/json")
  @Produces("application/json")
  CreateItemResponse createItem(@PathParam("systemId") @NotNull String systemId,
      @QueryParam("dryRun") Boolean dryRun, @HeaderParam("partNumber") Long partNumber,
      @HeaderParam("testCaseSelector") String testCaseSelector, @NotNull @Valid Item requestBody);

  class CreateItemResponse extends ResponseWrapper {
    private CreateItemResponse(Response delegate) {
      super(delegate);
    }

    public static CreateItemResponse with200ApplicationJson(Item entity) {
      return new CreateItemResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreateItemResponse with201() {
      return new CreateItemResponse(Response.status(201).build());
    }

    public static CreateItemResponse with204() {
      return new CreateItemResponse(Response.status(204).build());
    }

    public static CreateItemResponse with400ApplicationJson(Failure entity) {
      return new CreateItemResponse(Response.status(400).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreateItemResponse withApplicationJson(int status, Failure entity) {
      return new CreateItemResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static CreateItemResponse withCustomResponse(Response response) {
      return new CreateItemResponse(response);
    }
  }
}
