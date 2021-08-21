package de.rk42.openapi.codegen.crosscutting

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase
import de.rk42.openapi.codegen.Configuration

/**
 * Configures Logback logging.
 */
class LogbackConfigurator : ContextAwareBase(), Configurator {

  override fun configure(loggerContext: LoggerContext) {
    cachedLoggerContext = loggerContext

    loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).apply {
      addAppender(createConsoleAppender(loggerContext))
    }

    applyLoggingVerbosity(Configuration.Verbosity.NORMAL)
  }

  private fun createConsoleAppender(loggerContext: LoggerContext): ConsoleAppender<ILoggingEvent> {
    val loggingLayout = LoggingLayout().apply {
      context = loggerContext
      start()
    }

    val layoutWrappingEncoder = LayoutWrappingEncoder<ILoggingEvent>().apply {
      context = loggerContext
      layout = loggingLayout
    }

    return ConsoleAppender<ILoggingEvent>().apply {
      context = loggerContext
      name = "console"
      encoder = layoutWrappingEncoder
      start()
    }
  }

  companion object {

    private var cachedLoggerContext: LoggerContext? = null

    /**
     * Applies a logging configuration according to the supplied verbosity.
     */
    fun applyLoggingVerbosity(verbosity: Configuration.Verbosity) {
      val rootLoggerLevel = when (verbosity) {
        Configuration.Verbosity.VERBOSE -> Level.DEBUG
        Configuration.Verbosity.NORMAL -> Level.INFO
        Configuration.Verbosity.QUIET -> Level.WARN
      }

      rootLogger().level = rootLoggerLevel
      logger("io.swagger.v3").level = Level.WARN
    }

    private fun logger(name: String): Logger = cachedLoggerContext!!.getLogger(name)

    private fun rootLogger() = logger(Logger.ROOT_LOGGER_NAME)
  }
}