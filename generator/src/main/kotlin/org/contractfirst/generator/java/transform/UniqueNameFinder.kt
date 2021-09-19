package org.contractfirst.generator.java.transform

/**
 * Used to find unique names for types that might otherwise have duplicated names. 
 * 
 * This class is stateful, as it maintains a set of already encountered names.
 */
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