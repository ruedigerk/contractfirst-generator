package io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec

import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

/**
 * Superclass for Spock tests that do Spring integration testing.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = SpringIntegrationTestApplication)
abstract class SpringWebIntegrationSpecification extends Specification {

  static final String HOST = "localhost"
  static final String PORT = "17248"
  static final String BASE_URL = "http://$HOST:$PORT"

  private static final boolean VERBOSE = Boolean.parseBoolean(System.getenv("VERBOSE"))

  static {
    // Define the port for the embedded web server to listen on.
    System.setProperty("server.port", PORT)

    // Disable context caching as each test needs a different context anyway (each has its own RestController), and we can not have multiple web contexts
    // listening on the same port, as we're using WebEnvironment.DEFINED_PORT.
    System.setProperty("spring.test.context.cache.maxSize", "1")
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
