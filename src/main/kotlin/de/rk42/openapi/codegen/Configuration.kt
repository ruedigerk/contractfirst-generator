package de.rk42.openapi.codegen

/**
 * The configuration for a single run of the application.
 */
data class Configuration(
    val contractFile: String,
    val contractOutputFile: String,
    val outputDir: String,
    val outputContract: Boolean,
    val sourcePackage: String,
    val modelPrefix: String,
    val verbosity: Verbosity,
) {

  fun prettyPrint(indent: String = "\t"): String =
      """|contractFile='$contractFile'
         |contractOutputFile='$contractOutputFile'
         |outputDir='$outputDir'
         |outputContract=$outputContract
         |sourcePackage='$sourcePackage'
         |modelPrefix='$modelPrefix'
         |verbosity=$verbosity""".trimMargin().prependIndent(indent)

  enum class Verbosity {
    VERBOSE, NORMAL, QUIET
  }
}