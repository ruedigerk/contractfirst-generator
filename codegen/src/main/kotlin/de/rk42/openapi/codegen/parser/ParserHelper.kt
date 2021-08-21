package de.rk42.openapi.codegen.parser

object ParserHelper {

  fun <T> List<T>?.nullToEmpty(): List<T> = this ?: emptyList()

  fun <K, V> Map<K, V>?.nullToEmpty(): Map<K, V> = this ?: emptyMap()

  fun String?.normalize(): String? = if (this.isNullOrBlank()) null else this.trim()
}