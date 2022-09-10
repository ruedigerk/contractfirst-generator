package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * A Java type name, i.e. the combination of a package name and a simple name. 
 */
data class JavaTypeName(
    val packageName: String,
    val simpleName: String
) {

  override fun toString(): String = "$packageName.$simpleName"
}