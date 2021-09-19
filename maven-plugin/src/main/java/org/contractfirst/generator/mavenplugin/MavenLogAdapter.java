package org.contractfirst.generator.mavenplugin;

import org.apache.maven.plugin.logging.Log;
import org.contractfirst.generator.logging.LogAdapter;

/**
 * Logging adapter for Maven Log.
 */
class MavenLogAdapter implements LogAdapter {

  private final Log underlying;

  MavenLogAdapter(Log underlying) {
    this.underlying = underlying;
  }

  @Override
  public boolean isDebugEnabled() {
    return underlying.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    underlying.debug(msg);
  }

  @Override
  public void debug(String msg, Throwable error) {
    underlying.debug(msg, error);
  }

  @Override
  public boolean isInfoEnabled() {
    return underlying.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    underlying.info(msg);
  }

  @Override
  public void info(String msg, Throwable error) {
    underlying.info(msg, error);
  }

  @Override
  public boolean isWarnEnabled() {
    return underlying.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    underlying.warn(msg);
  }

  @Override
  public void warn(String msg, Throwable error) {
    underlying.warn(msg, error);
  }

  @Override
  public boolean isErrorEnabled() {
    return underlying.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    underlying.error(msg);
  }

  @Override
  public void error(String msg, Throwable error) {
    underlying.error(msg, error);
  }
}