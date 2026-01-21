package server.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
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
   * Second test operation for generic types, using the same generic return type as the other operation (test for bug in the generator).
   */
  @POST
  @Path("/items2")
  @Consumes("application/json")
  @Produces("application/json")
  FilterItems2Response filterItems2(@NotNull @Valid List<Item> requestBody);

  /**
   * Test for 204 response.
   */
  @POST
  @Path("/itemBinaries")
  @Consumes("application/json")
  @Produces("application/json")
  ChangeItemResponse changeItem(@NotNull @Valid Item requestBody);

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

  class FilterItems2Response extends ResponseWrapper {
    private FilterItems2Response(Response delegate) {
      super(delegate);
    }

    public static FilterItems2Response with200ApplicationJson(List<Item> entity) {
      return new FilterItems2Response(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterItems2Response withApplicationJson(int status, Failure entity) {
      return new FilterItems2Response(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static FilterItems2Response withCustomResponse(Response response) {
      return new FilterItems2Response(response);
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
}
