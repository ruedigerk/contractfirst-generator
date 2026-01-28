package io.github.ruedigerk.contractfirst.generator.openapi

import io.github.ruedigerk.contractfirst.generator.parser.Parseable

/**
 * Represents the unique identifier of a schema, using the schema position to uniquely identify it.
 */
class SchemaId private constructor(
  val position: Position,
  val source: Parseable,
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as SchemaId

    if (position != other.position) return false

    return true
  }

  override fun hashCode(): Int {
    return position.hashCode()
  }

  override fun toString(): String {
    return "SchemaId($position)"
  }

  companion object {

    operator fun invoke(parseable: Parseable) = SchemaId(parseable.position, parseable)
  }
}
