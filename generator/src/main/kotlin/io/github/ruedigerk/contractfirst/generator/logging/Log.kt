package io.github.ruedigerk.contractfirst.generator.logging

/**
 * Logger class acting as a wrapper around a slf4j logger, providing Kotlin convenience using inline functions and lambdas.
 * 
 * In the spirit of: https://github.com/MicroUtils/kotlin-logging
 */
class Log(val underlyingLogger: LogAdapter) {

  inline fun debug(msg: () -> String) {
    if (underlyingLogger.isDebugEnabled()) {
      underlyingLogger.debug(msg())
    }
  }

  inline fun debug(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isDebugEnabled()) {
      underlyingLogger.debug(msg(), throwable)
    }
  }

  inline fun info(msg: () -> String) {
    if (underlyingLogger.isInfoEnabled()) {
      underlyingLogger.info(msg())
    }
  }

  inline fun info(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isInfoEnabled()) {
      underlyingLogger.info(msg(), throwable)
    }
  }

  inline fun warn(msg: () -> String) {
    if (underlyingLogger.isWarnEnabled()) {
      underlyingLogger.warn(msg())
    }
  }

  inline fun warn(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isWarnEnabled()) {
      underlyingLogger.warn(msg(), throwable)
    }
  }

  inline fun error(msg: () -> String) {
    if (underlyingLogger.isErrorEnabled()) {
      underlyingLogger.error(msg())
    }
  }

  inline fun error(throwable: Throwable, msg: () -> String) {
    if (underlyingLogger.isErrorEnabled()) {
      underlyingLogger.error(msg(), throwable)
    }
  }
}
