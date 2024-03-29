package io.github.ruedigerk.contractfirst.generator.logging

import org.slf4j.Logger

/**
 * Logging adapter for SLF4J.
 */
class Slf4jLogAdapter(private val underlying: Logger) : LogAdapter {

  override fun isDebugEnabled(): Boolean {
    return underlying.isDebugEnabled
  }

  override fun debug(msg: String) {
    underlying.debug(msg)
  }

  override fun debug(msg: String, error: Throwable) {
    underlying.debug(msg, error)
  }

  override fun isInfoEnabled(): Boolean {
    return underlying.isInfoEnabled
  }

  override fun info(msg: String) {
    underlying.info(msg)
  }

  override fun info(msg: String, error: Throwable) {
    underlying.info(msg, error)
  }

  override fun isWarnEnabled(): Boolean {
    return underlying.isWarnEnabled
  }

  override fun warn(msg: String) {
    underlying.warn(msg)
  }

  override fun warn(msg: String, error: Throwable) {
    underlying.warn(msg, error)
  }

  override fun isErrorEnabled(): Boolean {
    return underlying.isErrorEnabled
  }

  override fun error(msg: String) {
    underlying.error(msg)
  }

  override fun error(msg: String, error: Throwable) {
    underlying.error(msg, error)
  }
}