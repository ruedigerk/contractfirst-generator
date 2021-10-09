package io.github.ruedigerk.contractfirst.generator.integrationtest.spec

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport
import io.github.ruedigerk.contractfirst.generator.integrationtest.EmbeddedJaxRsServer
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
  private io.github.ruedigerk.contractfirst.generator.integrationtest.EmbeddedJaxRsServer embeddedServer
  @Shared
  private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
      {
        log("CLIENT: $it")
      }
  )

  @Shared
  io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport restClientSupport = new io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport(new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build(), BASE_URL)

  abstract Class<?> getTestResource()

  static log(String msg) {
    if (VERBOSE) {
      println(msg)
    }
  }
  
  def setupSpec() {
    embeddedServer = new io.github.ruedigerk.contractfirst.generator.integrationtest.EmbeddedJaxRsServer(BASE_URL, getTestResource())
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