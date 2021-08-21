package de.rk42.openapi.codegen.crosscutting

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Logger class acting as a wrapper around an slf4j logger, providing Kotlin convenience using inline functions and lambdas.
 * 
 * In the spirit of: https://github.com/MicroUtils/kotlin-logging
 */
class Log(val underlyingLogger: Logger) {

  inline fun debug(msg: () -> String) {
    if (underlyingLogger.isDebugEnabled) {
      underlyingLogger.debug(msg())
    }
  }

  inline fun debug(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isDebugEnabled) {
      underlyingLogger.debug(msg(), throwable)
    }
  }

  inline fun info(msg: () -> String) {
    if (underlyingLogger.isInfoEnabled) {
      underlyingLogger.info(msg())
    }
  }

  inline fun info(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isInfoEnabled) {
      underlyingLogger.info(msg(), throwable)
    }
  }

  inline fun error(msg: () -> String) {
    if (underlyingLogger.isErrorEnabled) {
      underlyingLogger.error(msg())
    }
  }

  inline fun error(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isErrorEnabled) {
      underlyingLogger.error(msg(), throwable)
    }
  }

  companion object {

    /**
     * Retrieves the logger for the current class. For the idea, see: https://github.com/MicroUtils/kotlin-logging/issues/179
     */
    @Suppress("unused") // IDEA thinks that the generic parameter T is unused, but it is needed.
    inline fun <reified T> T.getLogger(): Log {
      val loggingClass = T::class.java
      return Log(LoggerFactory.getLogger(loggingClass))
    }
  }
}
