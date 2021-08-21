package de.rk42.openapi.codegen.model

/**
 * Represents a hint for a type name based on the location of the corresponding schema in the specification.
 */
data class NameHint(val path: List<String>) {

  operator fun div(child: String): NameHint = NameHint(path + child)

  fun removePrefix(prefix: NameHint): NameHint = NameHint(removePrefix(prefix.path, path))

  private fun removePrefix(prefix: List<String>, names: List<String>): List<String> =
      if (prefix.isEmpty() || names.isEmpty() || prefix.first() != names.first()) {
        names
      } else {
        removePrefix(prefix.drop(1), names.drop(1))
      }

  override fun toString(): String = path.toString()
  
  companion object {

    operator fun invoke(rootElement: String): NameHint = NameHint(listOf(rootElement))
  }
}
