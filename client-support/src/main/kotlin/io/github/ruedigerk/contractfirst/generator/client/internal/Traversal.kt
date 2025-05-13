package io.github.ruedigerk.contractfirst.generator.client.internal

/**
 * Helps with traversing object structures.
 */
internal object Traversal {

  /**
   * If value is a Collection, consumes each non-null element of it, otherwise if value is not a collection, consumes it, if it is non-null.
   */
  fun traverse(value: Any?, consume: (Any) -> Unit) {
    if (value is Collection<*>) {
      value.filterNotNull().forEach(consume)
    } else if(value != null) {
      consume(value)
    }
  }
}