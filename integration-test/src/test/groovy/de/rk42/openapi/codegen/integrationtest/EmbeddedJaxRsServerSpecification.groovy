package de.rk42.openapi.codegen.integrationtest

import de.rk42.openapi.codegen.client.RestClientSupport
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

  @Shared
  private EmbeddedJaxRsServer embeddedServer
  @Shared
  private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor({ System.out.println("CLIENT: $it") })

  @Shared
  RestClientSupport restClientSupport = new RestClientSupport(new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build(), BASE_URL)

  abstract Class<?> getTestResource()

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
