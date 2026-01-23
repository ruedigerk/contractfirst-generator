package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * A Java type name, i.e. the combination of a package name and a simple name.
 */
data class JavaTypeName(
    val packageName: String,
    val simpleName: String
) {

  override fun toString(): String = "$packageName.$simpleName"

  /**
   * Defines the names of commonly used Java types.
   */
  companion object {

    val OBJECT = JavaTypeName("java.lang", "Object")
    val STRING = JavaTypeName("java.lang", "String")
    val BOOLEAN = JavaTypeName("java.lang", "Boolean")
    val INPUT_STREAM = JavaTypeName("java.io", "InputStream")

    val INTEGER = JavaTypeName("java.lang", "Integer")
    val LONG = JavaTypeName("java.lang", "Long")
    val BIG_INTEGER = JavaTypeName("java.math", "BigInteger")

    val FLOAT = JavaTypeName("java.lang", "Float")
    val DOUBLE = JavaTypeName("java.lang", "Double")
    val BIG_DECIMAL = JavaTypeName("java.math", "BigDecimal")

    val LOCAL_DATE = JavaTypeName("java.time", "LocalDate")
    val OFFSET_DATE_TIME = JavaTypeName("java.time", "OffsetDateTime")

    val LIST = JavaTypeName("java.util", "List")
    val MAP = JavaTypeName("java.util", "Map")
    val SET = JavaTypeName("java.util", "Set")
    
    val ARRAY_LIST = JavaTypeName("java.util", "ArrayList")
    val HASH_MAP = JavaTypeName("java.util", "HashMap")
    val HASH_SET = JavaTypeName("java.util", "HashSet")
  }
}
