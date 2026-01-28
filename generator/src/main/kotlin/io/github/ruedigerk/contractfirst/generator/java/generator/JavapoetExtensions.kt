package io.github.ruedigerk.contractfirst.generator.java.generator

object JavapoetExtensions {

  fun <T> T.doIf(condition: Boolean, action: T.() -> Unit): T {
    if (condition) {
      action(this)
    }
    return this
  }

  fun <T, V> T.doIfNotNull(value: V?, action: T.(V) -> Unit): T {
    if (value != null) {
      action(this, value)
    }
    return this
  }
}
