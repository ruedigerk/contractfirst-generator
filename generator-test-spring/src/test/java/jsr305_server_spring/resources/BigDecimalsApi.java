package jsr305_server_spring.resources;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import jsr305_server_spring.model.Failure;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface BigDecimalsApi {
  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/bigDecimals",
      produces = "application/json"
  )
  GetNumberResponse getNumber(@RequestParam("decimalNumber") @NotNull BigDecimal decimalNumber);

  class GetNumberResponse extends ResponseWrapper {
    private GetNumberResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetNumberResponse with200ApplicationJson(BigDecimal entity) {
      return new GetNumberResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetNumberResponse withApplicationJson(int status, Failure entity) {
      return new GetNumberResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetNumberResponse withCustomResponse(ResponseEntity response) {
      return new GetNumberResponse(response);
    }
  }
}
