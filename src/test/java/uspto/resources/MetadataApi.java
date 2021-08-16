package uspto.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import uspto.model.DataSetList;
import uspto.resources.support.ResponseWrapper;

@Path("")
public interface MetadataApi {
  /**
   * List available data sets
   */
  @GET
  @Path("/")
  @Produces("application/json")
  ListDataSetsResponse listDataSets();

  /**
   * This GET API returns the list of all the searchable field names that are in the oa_citations. Please see the 'fields' attribute which returns an array of field names. Each field or a combination of fields can be searched using the syntax options shown below.
   *
   * @param dataset Name of the dataset. In this case, the default value is oa_citations
   * @param version Version of the dataset.
   */
  @GET
  @Path("/{dataset}/{version}/fields")
  @Produces("application/json")
  ListSearchableFieldsResponse listSearchableFields(@PathParam("dataset") @NotNull String dataset,
      @PathParam("version") @NotNull String version);

  class ListDataSetsResponse extends ResponseWrapper {
    private ListDataSetsResponse(Response delegate) {
      super(delegate);
    }

    public static ListDataSetsResponse with200ApplicationJson(DataSetList entity) {
      return new ListDataSetsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListDataSetsResponse withCustomResponse(Response response) {
      return new ListDataSetsResponse(response);
    }
  }

  class ListSearchableFieldsResponse extends ResponseWrapper {
    private ListSearchableFieldsResponse(Response delegate) {
      super(delegate);
    }

    public static ListSearchableFieldsResponse with200ApplicationJson(String entity) {
      return new ListSearchableFieldsResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListSearchableFieldsResponse with404ApplicationJson(String entity) {
      return new ListSearchableFieldsResponse(Response.status(404).header("Content-Type", "application/json").entity(entity).build());
    }

    public static ListSearchableFieldsResponse withCustomResponse(Response response) {
      return new ListSearchableFieldsResponse(response);
    }
  }
}
