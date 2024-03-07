package io.github.ruedigerk.contractfirst.generator.compat_1_7_test.spec

import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor
import io.github.ruedigerk.contractfirst.generator.integrationtest.EmbeddedJaxRsServer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Shared
import spock.lang.Specification 

/**
 * Superclass for Spock tests that use an embedded JAX-RS server.
 */
abstract class EmbeddedJaxRsServerSpecification extends Specification {

  static final String HOST = "localhost:17249"
  static final String BASE_URL = "http://$HOST"

  private static final boolean VERBOSE = Boolean.parseBoolean(System.getenv("VERBOSE"))

  @Shared
  private EmbeddedJaxRsServer embeddedServer
  @Shared
  private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
      {
        log("CLIENT: $it")
      }
  )

  @Shared
  OkHttpClient okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build()
  @Shared
  ApiRequestExecutor apiClientSupport = new ApiRequestExecutor(okHttpClient, BASE_URL)

  abstract Class<?> getTestResource()

  static log(String msg) {
    if (VERBOSE) {
      println(msg)
    }
  }

  def setupSpec() {
    embeddedServer = new EmbeddedJaxRsServer(BASE_URL, getTestResource())
    embeddedServer.startServer()
  }

  def setup() {
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
  }

  def cleanupSpec() {
    embeddedServer.stopServer()
  }

  def setLoggingInterceptorLevel(HttpLoggingInterceptor.Level level) {
    loggingInterceptor.setLevel(level)
  }
}
