package server.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.Response;
import java.util.List;
import server.resources.support.ResponseWrapper;

@Path("")
public interface MultipartRequestBodyApi {
  /**
   * A test case for an x-www-form-urlencoded encoded request body.
   */
  @POST
  @Path("/formEncodedRequestBody")
  @Consumes("application/x-www-form-urlencoded")
  @Produces
  FormEncodedRequestBodyResponse formEncodedRequestBody(
      @FormParam("stringProperty") String stringProperty,
      @FormParam("integerProperty") Long integerProperty,
      @FormParam("enumProperty") String enumProperty);

  /**
   * A test case for a multipart/form-data encoded request body.
   *
   * @param testSelector Selects the assertions to perform on the server.
   */
  @POST
  @Path("/multipartRequestBody")
  @Consumes("multipart/form-data")
  @Produces
  MultipartRequestBodyResponse multipartRequestBody(
      @QueryParam("testSelector") @NotNull String testSelector,
      @FormParam("stringProperty") String stringProperty,
      @FormParam("integerProperty") Long integerProperty,
      @FormParam("objectProperty") EntityPart objectProperty,
      @FormParam("firstBinary") EntityPart firstBinary,
      @FormParam("additionalBinaries") List<EntityPart> additionalBinaries);

  class FormEncodedRequestBodyResponse extends ResponseWrapper {
    private FormEncodedRequestBodyResponse(Response delegate) {
      super(delegate);
    }

    public static FormEncodedRequestBodyResponse with204() {
      return new FormEncodedRequestBodyResponse(Response.status(204).build());
    }

    public static FormEncodedRequestBodyResponse withCustomResponse(Response response) {
      return new FormEncodedRequestBodyResponse(response);
    }
  }

  class MultipartRequestBodyResponse extends ResponseWrapper {
    private MultipartRequestBodyResponse(Response delegate) {
      super(delegate);
    }

    public static MultipartRequestBodyResponse with204() {
      return new MultipartRequestBodyResponse(Response.status(204).build());
    }

    public static MultipartRequestBodyResponse withCustomResponse(Response response) {
      return new MultipartRequestBodyResponse(response);
    }
  }
}
