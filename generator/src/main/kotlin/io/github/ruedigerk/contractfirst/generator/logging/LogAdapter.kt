package io.github.ruedigerk.contractfirst.generator.logging

/**
 * Interface for adapting to various logging systems, especially Logback/SLF4J and Maven-Log.
 */
interface LogAdapter {

  fun isDebugEnabled(): Boolean

  fun debug(msg: String)

  fun debug(msg: String, error: Throwable)

  fun isInfoEnabled(): Boolean

  fun info(msg: String)

  fun info(msg: String, error: Throwable)

  fun isWarnEnabled(): Boolean

  fun warn(msg: String)

  fun warn(msg: String, error: Throwable)

  fun isErrorEnabled(): Boolean

  fun error(msg: String)

  fun error(msg: String, error: Throwable)
}
