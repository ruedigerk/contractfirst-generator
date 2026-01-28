package io.github.ruedigerk.contractfirst.generator.java.transform

/**
 * Extension functions for collections.
 */
object Collections {

  @Suppress("UNCHECKED_CAST")
  fun <T> List<T?>.takeIfAllElementsNotNull(): List<T>? = takeIf { list -> list.all { it != null } }?.let { it as List<T> }
}
