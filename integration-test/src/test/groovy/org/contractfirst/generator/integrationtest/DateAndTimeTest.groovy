package org.contractfirst.generator.integrationtest

import org.contractfirst.generator.integrationtest.generated.client.api.TimeApiClient
import org.contractfirst.generator.integrationtest.generated.client.model.CClock
import org.contractfirst.generator.integrationtest.generated.client.model.CClockResponse
import org.contractfirst.generator.integrationtest.generated.server.model.SClock
import org.contractfirst.generator.integrationtest.generated.server.model.SClockResponse
import org.contractfirst.generator.integrationtest.generated.server.resources.TimeApi
import org.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * Tests date and date-time formats used at different places in the contract.
 */
class DateAndTimeTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  TimeApiClient restClient = new TimeApiClient(restClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Date and date-time formats are supported in entities and parameters"() {
    given:
    def time1 = LocalDate.parse("2020-01-01")
    def time2 = OffsetDateTime.parse("2020-01-01T00:00:00+00")
    def pathTime = LocalDate.parse("2020-01-02")
    def queryTime1 = LocalDate.parse("2020-01-03")
    def queryTime2 = OffsetDateTime.parse("2020-01-03T01:00:01+00")
    def headerTime1 = LocalDate.parse("2020-01-04")
    def headerTime2 = OffsetDateTime.parse("2020-01-04T02:00:02+00")
    def input = new CClock(time1: time1, time2: time2)

    when:
    CClockResponse clockResponse = restClient.updateTime(pathTime, queryTime1, queryTime2, headerTime1, headerTime2, input)

    then:
    clockResponse.time1 == time1
    clockResponse.time2 == time2
    clockResponse.pathTime == pathTime
    clockResponse.queryTime1 == queryTime1
    clockResponse.queryTime2 == queryTime2
    clockResponse.headerTime1 == headerTime1
    clockResponse.headerTime2 == headerTime2
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements TimeApi {

    @Override
    UpdateTimeResponse updateTime(
        LocalDate timeId,
        LocalDate queryTimeA,
        OffsetDateTime queryTimeB,
        LocalDate headerTimeA,
        OffsetDateTime headerTimeB,
        SClock requestBody
    ) {
      return UpdateTimeResponse.with200ApplicationJson(
          new SClockResponse(
              time1: requestBody.time1,
              time2: requestBody.time2,
              pathTime: timeId,
              queryTime1: queryTimeA,
              queryTime2: queryTimeB,
              headerTime1: headerTimeA,
              headerTime2: headerTimeB
          )
      )
    }
  }
}
