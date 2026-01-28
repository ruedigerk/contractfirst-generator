package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import org.jetbrains.annotations.NotNull

class NoLoggingLogAdapter implements LogAdapter {

  @Override
  boolean isDebugEnabled() {
    return false
  }

  @Override
  void debug(@NotNull String msg) {
  }

  @Override
  void debug(@NotNull String msg, @NotNull Throwable error) {
  }

  @Override
  boolean isInfoEnabled() {
    return false
  }

  @Override
  void info(@NotNull String msg) {
  }

  @Override
  void info(@NotNull String msg, @NotNull Throwable error) {
  }

  @Override
  boolean isWarnEnabled() {
    return false
  }

  @Override
  void warn(@NotNull String msg) {
  }

  @Override
  void warn(@NotNull String msg, @NotNull Throwable error) {
  }

  @Override
  boolean isErrorEnabled() {
    return false
  }

  @Override
  void error(@NotNull String msg) {
  }

  @Override
  void error(@NotNull String msg, @NotNull Throwable error) {
  }
}