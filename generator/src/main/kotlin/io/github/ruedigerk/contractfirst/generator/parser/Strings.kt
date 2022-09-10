package io.github.ruedigerk.contractfirst.generator.parser

/**
 * Helper for manipulating Strings.
 */
object Strings {

  fun String?.normalize(): String? = if (this.isNullOrBlank()) null else this.trim()
}