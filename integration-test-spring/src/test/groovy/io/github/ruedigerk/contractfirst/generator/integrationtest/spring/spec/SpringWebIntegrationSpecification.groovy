package io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec

import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * Superclass for Spock tests that do Spring integration testing.
 *
 * DirtiesContext is used to prevent caching the context, as each test uses different beans, preventing reuse anyway. Also, avoids subsequent tests failing with
 * a "Port already in use" error due to using WebEnvironment.DEFINED_PORT.
 *
 * See: https://stackoverflow.com/a/50412277
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SpringIntegrationTestApplication)
abstract class SpringWebIntegrationSpecification extends Specification {

  static final String HOST = "localhost"
  static final String PORT = "17248"
  static final String BASE_URL = "http://$HOST:$PORT"

  private static final boolean VERBOSE = Boolean.parseBoolean(System.getenv("VERBOSE"))

  static {
    // Define the port for the embedded web server to listen on.
    System.setProperty("server.port", PORT)

    // Disable the Spring logo to be printed.
    System.setProperty("spring.main.banner-mode", "off")
  }

  @Shared
  private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
      {
        log("CLIENT: $it")
      }
  )

  @Shared
  OkHttpClient okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build()
  @Shared
  ApiRequestExecutor apiRequestExecutor = new ApiRequestExecutor(okHttpClient, BASE_URL)

  static log(String msg) {
    if (VERBOSE) {
      println(msg)
    }
  }

  def setup() {
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
  }

  def setLoggingInterceptorLevel(HttpLoggingInterceptor.Level level) {
    loggingInterceptor.setLevel(level)
  }
}
