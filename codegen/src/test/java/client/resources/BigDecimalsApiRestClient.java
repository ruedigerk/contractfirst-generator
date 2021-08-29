package client.resources;

import de.rk42.openapi.codegen.client.DefinedResponse;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.RestClientIoException;
import de.rk42.openapi.codegen.client.RestClientSupport;
import de.rk42.openapi.codegen.client.RestClientUndefinedResponseException;
import de.rk42.openapi.codegen.client.RestClientValidationException;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.ParameterLocation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
import java.math.BigDecimal;

public class BigDecimalsApiRestClient {
  private final RestClientSupport support;

  public BigDecimalsApiRestClient(RestClientSupport support) {
    this.support = support;
  }

  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  public BigDecimal getNumber(BigDecimal decimalNumber) throws RestClientIoException,
      RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = getNumberWithResponse(decimalNumber);
    DefinedResponse response = genericResponse.asDefinedResponse();


    return (BigDecimal) response.getEntity();
  }

  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  public GenericResponse getNumberWithResponse(BigDecimal decimalNumber) throws
      RestClientIoException, RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/bigDecimals", "GET");

    builder.parameter("decimalNumber", ParameterLocation.QUERY, true, decimalNumber);

    builder.response(StatusCode.of(200), "application/json", BigDecimal.class);

    return support.executeRequest(builder.build());
  }
}
