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
import server.model.Failure;
import server.model.Item;
import server.resources.support.ResponseWrapper;

@Path("")
public interface PayloadVariantsApi {
  /**
   * Test operation for generating generic types, e.g. List of Item.
   */
  @POST
  @Path("/items")
  @Consumes("application/json")
  @Produces("application/json")
  FilterItemsResponse filterItems(@NotNull @Valid List<Item> requestBody);

  /**
   * Test binary input and output.
   */
  @PUT
  @Path("/itemBinaries")
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
  @Path("/itemBinaries")
  @Consumes("application/json")
  @Produces("application/json")
  ChangeItemResponse changeItem(@NotNull @Valid Item requestBody);

  class FilterItemsResponse extends ResponseWrapper {
    private FilterItemsResponse(Response delegate) {
      super(delegate);
    }

    public static FilterItemsResponse with200ApplicationJson(List<Item> entity) {
      return new FilterItemsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterItemsResponse withApplicationJson(int status, Failure entity) {
      return new FilterItemsResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterItemsResponse withCustomResponse(Response response) {
      return new FilterItemsResponse(response);
    }
  }

  class UploadAndReturnBinaryResponse extends ResponseWrapper {
    private UploadAndReturnBinaryResponse(Response delegate) {
      super(delegate);
    }

    public static UploadAndReturnBinaryResponse with200ApplicationOctetStream(InputStream entity) {
      return new UploadAndReturnBinaryResponse(Response.status(200).header("Content-Type", "application/octet-stream").entity(entity).build());
    }

    public static UploadAndReturnBinaryResponse withApplicationJson(int status, Failure entity) {
      return new UploadAndReturnBinaryResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static UploadAndReturnBinaryResponse withCustomResponse(Response response) {
      return new UploadAndReturnBinaryResponse(response);
    }
  }

  class ChangeItemResponse extends ResponseWrapper {
    private ChangeItemResponse(Response delegate) {
      super(delegate);
    }

    public static ChangeItemResponse with204() {
      return new ChangeItemResponse(Response.status(204).build());
    }

    public static ChangeItemResponse withApplicationJson(int status, Failure entity) {
      return new ChangeItemResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ChangeItemResponse withCustomResponse(Response response) {
      return new ChangeItemResponse(response);
    }
  }
}
