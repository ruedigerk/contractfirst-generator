package org.contractfirst.generator.integrationtest.spec

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.contractfirst.generator.client.ApiClientSupport
import org.contractfirst.generator.integrationtest.EmbeddedJaxRsServer
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
  ApiClientSupport restClientSupport = new ApiClientSupport(new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build(), BASE_URL)

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
