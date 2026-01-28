package io.github.ruedigerk.contractfirst.generator.parser

/**
 * Trims whitespace and returns null for empty or blank strings.
 */
fun String?.normalize(): String? = if (this.isNullOrBlank()) null else this.trim()
