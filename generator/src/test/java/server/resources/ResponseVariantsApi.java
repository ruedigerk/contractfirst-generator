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
