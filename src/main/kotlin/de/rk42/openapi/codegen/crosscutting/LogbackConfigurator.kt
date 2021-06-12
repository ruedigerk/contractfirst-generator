package de.rk42.openapi.codegen.crosscutting

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase

/**
 * Configures Logback logging.
 */
class LogbackConfigurator : ContextAwareBase(), Configurator {

  override fun configure(loggerContext: LoggerContext) {
    cachedLoggerContext = loggerContext

    loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).apply {
      addAppender(createConsoleAppender(loggerContext))
    }

    defaultLogLevels()
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

    fun defaultLogLevels() {
      rootLogger().level = Level.INFO
      logger("io.swagger.v3").level = Level.WARN
    }
    
    fun verboseLogLevels() {
      rootLogger().level = Level.DEBUG
    }

    fun quietLogLevels() {
      rootLogger().level = Level.WARN
    }

    private fun logger(name: String): Logger = cachedLoggerContext!!.getLogger(name)

    private fun rootLogger() = logger(Logger.ROOT_LOGGER_NAME)
  }
}