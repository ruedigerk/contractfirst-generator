package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName

/**
 * Used to find unique names for types that might otherwise have duplicated names. 
 * 
 * This class is stateful, as it maintains a set of already encountered names.
 */
class TypeNameUniquifier {

  private val names: MutableSet<JavaTypeName> = mutableSetOf()

  fun toUniqueName(name: JavaTypeName): JavaTypeName {
    var candidate = name
    var suffix = 2

    while (!names.add(candidate)) {
      candidate = JavaTypeName(name.packageName, name.simpleName + suffix)
      suffix++
    }

    return candidate
  }
}