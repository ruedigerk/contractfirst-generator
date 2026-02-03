package multipart_server_spring.resources;

import jakarta.validation.Valid;
import java.util.List;
import multipart_server_spring.model.MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty;
import multipart_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Validated
public interface MultipartRequestBodyApi {
  /**
   * A test case for an x-www-form-urlencoded encoded request body.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/formEncodedRequestBody",
      consumes = "application/x-www-form-urlencoded"
  )
  FormEncodedRequestBodyResponse formEncodedRequestBody(
      @RequestParam(name = "stringProperty", required = false) String stringProperty,
      @RequestParam(name = "integerProperty", required = false) Long integerProperty,
      @RequestParam(name = "enumProperty", required = false) String enumProperty);

  /**
   * A test case for a multipart/form-data encoded request body.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/multipartRequestBody",
      consumes = "multipart/form-data"
  )
  MultipartRequestBodyResponse multipartRequestBody(
      @RequestParam(name = "stringProperty", required = false) String stringProperty,
      @RequestParam(name = "integerProperty", required = false) Long integerProperty,
      @RequestPart(name = "objectProperty", required = false) @Valid MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty objectProperty,
      @RequestPart(name = "firstBinary", required = false) MultipartFile firstBinary,
      @RequestPart(name = "additionalBinaries", required = false) List<MultipartFile> additionalBinaries);

  class FormEncodedRequestBodyResponse extends ResponseWrapper {
    private FormEncodedRequestBodyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static FormEncodedRequestBodyResponse with204() {
      return new FormEncodedRequestBodyResponse(ResponseEntity.status(204).build());
    }

    public static FormEncodedRequestBodyResponse withCustomResponse(ResponseEntity response) {
      return new FormEncodedRequestBodyResponse(response);
    }
  }

  class MultipartRequestBodyResponse extends ResponseWrapper {
    private MultipartRequestBodyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static MultipartRequestBodyResponse with204() {
      return new MultipartRequestBodyResponse(ResponseEntity.status(204).build());
    }

    public static MultipartRequestBodyResponse withCustomResponse(ResponseEntity response) {
      return new MultipartRequestBodyResponse(response);
    }
  }
}
