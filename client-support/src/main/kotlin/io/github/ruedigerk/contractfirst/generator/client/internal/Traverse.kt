package io.github.ruedigerk.contractfirst.generator.client.internal

/**
 * If value is a Collection, consumes each non-null element of it, otherwise if value is not a collection, consumes it, if it is non-null.
 */
internal fun traverse(value: Any?, consume: (Any) -> Unit) {
  when {
    value is Collection<*> -> value.filterNotNull().forEach(consume)
    value != null -> consume(value)
  }
}
