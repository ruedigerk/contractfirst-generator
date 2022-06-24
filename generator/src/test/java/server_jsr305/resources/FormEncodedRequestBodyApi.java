package server_jsr305.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server_jsr305.model.FormEncodedRequestBodyRequestBodyFieldC;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface FormEncodedRequestBodyApi {
  /**
   * A test case for an x-www-form-urlencoded encoded request body.
   */
  @POST
  @Path("/formEncodedRequestBody")
  @Consumes("application/x-www-form-urlencoded")
  @Produces
  FormEncodedRequestBodyResponse formEncodedRequestBody(@FormParam("fieldA") String fieldA,
      @FormParam("fieldB") String fieldB,
      @FormParam("fieldC") FormEncodedRequestBodyRequestBodyFieldC fieldC);

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
}
