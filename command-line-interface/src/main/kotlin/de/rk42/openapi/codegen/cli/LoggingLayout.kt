package de.rk42.openapi.codegen.cli

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase

/**
 * Basic logging layout for the application.
 */
class LoggingLayout : LayoutBase<ILoggingEvent>() {

  private val throwableProxyConverter = ThrowableProxyConverter()

  override fun doLayout(event: ILoggingEvent): String {
    if (!isStarted) {
      return CoreConstants.EMPTY_STRING
    }

    val builder = StringBuilder()

    if (event.level == Level.WARN) {
      builder.append("Warning: ")
    } else if (event.level == Level.ERROR) {
      builder.append("Error: ")
    }

    builder.append(event.formattedMessage)
    builder.append(CoreConstants.LINE_SEPARATOR)

    val throwable = event.throwableProxy
    if (throwable != null) {
      val stackTrace: String = throwableProxyConverter.convert(event)
      builder.append(stackTrace)
    }

    return builder.toString()
  }
}