package de.rk42.openapi.codegen.java.transform

class UniqueNameFinder {

  private val names: MutableSet<String> = mutableSetOf()

  fun toUniqueName(name: String): String {
    var candidate = name
    var suffix = 2

    while (!names.add(candidate)) {
      candidate = name + suffix
      suffix++
    }

    return candidate
  }
}