package de.rk42.openapi.codegen.model

/**
 * Represents a hint for a type name based on the location of the corresponding schema in the specification.
 */
data class NameHint(val path: List<String>) {

  operator fun div(child: String): NameHint = NameHint(path + child)

  companion object {

    operator fun invoke(rootElement: String): NameHint = NameHint(listOf(rootElement))
  }
}
