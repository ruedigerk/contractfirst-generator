package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.java.Identifiers.toJavaTypeIdentifier

class UniqueNameConverter {

  private val typeNames: MutableSet<String> = mutableSetOf()

  fun toUniqueTypeName(name: String): String {
    val identifier = name.toJavaTypeIdentifier()
    var candidate = identifier
    var suffix = 2

    while (!typeNames.add(candidate)) {
      candidate = identifier + suffix
      suffix++
    }

    return candidate
  }
}