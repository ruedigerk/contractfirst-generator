package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.BigDecimalsApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.BigDecimalsApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject
import spock.lang.Unroll

/**
 * Tests serialization of schema type number to BigDecimal.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class BigDecimalTest extends SpringWebIntegrationSpecification {

  @Subject
  BigDecimalsApiClient apiClient = new BigDecimalsApiClient(apiRequestExecutor)

  @Unroll
  @SuppressWarnings('ChangeToOperator')
  def "Test BigDecimal as parameter: #input"() {
    when:
    BigDecimal response = apiClient.getNumber(number)

    then:
    response.equals(number)

    where:
    input << ["0", "0.00", "1e10", "1e-10", "123.45", "12345678901234567890.1234567890"]
    number = new BigDecimal(input)
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements BigDecimalsApi {

    @Override
    GetNumberResponse getNumber(BigDecimal decimalNumber) {
      return GetNumberResponse.with200ApplicationJson(decimalNumber)
    }
  }
}
