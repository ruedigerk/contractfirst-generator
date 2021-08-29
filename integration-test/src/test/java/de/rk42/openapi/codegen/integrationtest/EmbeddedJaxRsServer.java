package de.rk42.openapi.codegen.integrationtest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used for starting and stopping an embedded JAX-RS enabled server.
 */
public class EmbeddedJaxRsServer {

  static {
    // Remove existing handlers attached to JDK root logger, to avoid duplicated log messages in the output.
    SLF4JBridgeHandler.removeHandlersForRootLogger();

    // Install jul-to-slf4j bridge handler to redirect JDK logging to slf4j.
    SLF4JBridgeHandler.install();
  }

  private final String baseUrl;
  private final List<Class<?>> jaxRsResourceClasses;
  private HttpServer httpServer;

  /**
   * Instantiates a new EmbeddedJaxRsServer with the supplied base URL and JAX-RS resource classes.
   *
   * @param baseUrl              the base URL the server should listen on for HTTP requests.
   * @param jaxRsResourceClasses the JAX-RS resource classes whose API the server should expose.
   */
  public EmbeddedJaxRsServer(String baseUrl, Class<?>... jaxRsResourceClasses) {
    this.baseUrl = baseUrl;
    this.jaxRsResourceClasses = Stream.of(jaxRsResourceClasses).collect(Collectors.toList());
  }

  /**
   * Starts the embedded HTTP server exposing the JAX-RS resources defined.
   */
  public synchronized void startServer() {
    if (httpServer != null) {
      throw new IllegalStateException("Server already started");
    }

    // Register GSON for serializing and deserializing JSON
    ResourceConfig resourceConfig = new ResourceConfig();
    resourceConfig.register(GsonMessageBodyHandler.class);
    resourceConfig.register(DateFormatsJaxRsParamConverterProvider.class);
    resourceConfig.register(JaxRsExceptionMapper.class);

    // Register resource classes
    jaxRsResourceClasses.forEach(resourceConfig::register);

    // TODO: Use JdkHttpServerFactory instead of Grizzly? Is it faster to start up? (also needs other dependencies)
    // Create and start a new instance of grizzly http server.
    httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig);
  }

  /**
   * Stop the embedded server.
   */
  public synchronized void stopServer() {
    if (httpServer != null) {
      httpServer.shutdownNow();
      httpServer = null;
    } else {
      throw new IllegalStateException("Server not started");
    }
  }
}
