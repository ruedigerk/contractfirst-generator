package io.github.ruedigerk.contractfirst.generator.integrationtest.spec

import io.github.ruedigerk.contractfirst.generator.server.DateFormatsParamConverterProvider
import io.github.ruedigerk.contractfirst.generator.server.GsonMessageBodyHandler
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.slf4j.bridge.SLF4JBridgeHandler

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Used for starting and stopping an embedded JAX-RS enabled server.
 */
class EmbeddedJaxRsServer {

  static {
    // Remove existing handlers attached to JDK root logger, to avoid duplicated log messages in the output.
    SLF4JBridgeHandler.removeHandlersForRootLogger()

    // Install jul-to-slf4j bridge handler to redirect JDK logging to slf4j.
    SLF4JBridgeHandler.install()
  }

  private final String baseUrl
  private final List<Class<?>> jaxRsResourceClasses
  private HttpServer httpServer

  /**
   * Instantiates a new EmbeddedJaxRsServer with the supplied base URL and JAX-RS resource classes.
   *
   * @param baseUrl              the base URL the server should listen on for HTTP requests.
   * @param jaxRsResourceClasses the JAX-RS resource classes whose API the server should expose.
   */
  EmbeddedJaxRsServer(String baseUrl, Class<?>... jaxRsResourceClasses) {
    this.baseUrl = baseUrl
    this.jaxRsResourceClasses = Stream.of(jaxRsResourceClasses).collect(Collectors.toList())
  }

  /**
   * Starts the embedded HTTP server exposing the JAX-RS resources defined.
   */
  synchronized void startServer() {
    if (httpServer != null) {
      throw new IllegalStateException("Server already started")
    }

    // Register GSON for serializing and deserializing JSON
    ResourceConfig resourceConfig = new ResourceConfig()
    resourceConfig.register(GsonMessageBodyHandler)
    resourceConfig.register(DateFormatsParamConverterProvider)
    resourceConfig.register(JaxRsExceptionMapper)

    // Register resource classes
    jaxRsResourceClasses.forEach(resourceConfig::register)

    // Create and start a new instance of grizzly http server.
    httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig)
  }

  /**
   * Stop the embedded server.
   */
  synchronized void stopServer() {
    if (httpServer != null) {
      httpServer.shutdownNow()
      httpServer = null
    } else {
      throw new IllegalStateException("Server not started")
    }
  }
}
