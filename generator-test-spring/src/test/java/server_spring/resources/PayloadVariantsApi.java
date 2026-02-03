package server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import server_spring.model.RestFailure;
import server_spring.model.RestItem;
import server_spring.resources.support.ResponseWrapper;

@Validated
public interface PayloadVariantsApi {
  /**
   * Test operation for generating generic types, e.g. List of Item.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/items",
      consumes = "application/json",
      produces = "application/json"
  )
  FilterItemsResponse filterItems(@RequestBody @NotNull @Valid List<RestItem> requestBody);

  /**
   * Second test operation for generic types, using the same generic return type as the other operation (test for bug in the generator).
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/items2",
      consumes = "application/json",
      produces = "application/json"
  )
  FilterItems2Response filterItems2(@RequestBody @NotNull @Valid List<RestItem> requestBody);

  /**
   * Test for 204 response.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/itemBinaries",
      consumes = "application/json",
      produces = "application/json"
  )
  ChangeItemResponse changeItem(@RequestBody @NotNull @Valid RestItem requestBody);

  /**
   * Test binary input and output.
   */
  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/itemBinaries",
      consumes = "application/octet-stream",
      produces = {
          "application/json",
          "application/octet-stream"
      }
  )
  UploadAndReturnBinaryResponse uploadAndReturnBinary(@RequestBody @NotNull Resource requestBody);

  class FilterItemsResponse extends ResponseWrapper {
    private FilterItemsResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static FilterItemsResponse with200ApplicationJson(List<RestItem> entity) {
      return new FilterItemsResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static FilterItemsResponse withApplicationJson(int status, RestFailure entity) {
      return new FilterItemsResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static FilterItemsResponse withCustomResponse(ResponseEntity response) {
      return new FilterItemsResponse(response);
    }
  }

  class FilterItems2Response extends ResponseWrapper {
    private FilterItems2Response(ResponseEntity delegate) {
      super(delegate);
    }

    public static FilterItems2Response with200ApplicationJson(List<RestItem> entity) {
      return new FilterItems2Response(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static FilterItems2Response withApplicationJson(int status, RestFailure entity) {
      return new FilterItems2Response(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static FilterItems2Response withCustomResponse(ResponseEntity response) {
      return new FilterItems2Response(response);
    }
  }

  class ChangeItemResponse extends ResponseWrapper {
    private ChangeItemResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static ChangeItemResponse with204() {
      return new ChangeItemResponse(ResponseEntity.status(204).build());
    }

    public static ChangeItemResponse withApplicationJson(int status, RestFailure entity) {
      return new ChangeItemResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static ChangeItemResponse withCustomResponse(ResponseEntity response) {
      return new ChangeItemResponse(response);
    }
  }

  class UploadAndReturnBinaryResponse extends ResponseWrapper {
    private UploadAndReturnBinaryResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static UploadAndReturnBinaryResponse with200ApplicationOctetStream(Resource entity) {
      return new UploadAndReturnBinaryResponse(ResponseEntity.status(200).header("Content-Type", "application/octet-stream").body(entity));
    }

    public static UploadAndReturnBinaryResponse withApplicationJson(int status,
        RestFailure entity) {
      return new UploadAndReturnBinaryResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static UploadAndReturnBinaryResponse withCustomResponse(ResponseEntity response) {
      return new UploadAndReturnBinaryResponse(response);
    }
  }
}
