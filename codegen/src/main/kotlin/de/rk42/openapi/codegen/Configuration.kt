package de.rk42.openapi.codegen

/**
 * The configuration for a single run of the application.
 */
data class Configuration(
    val contractFile: String,
    val contractOutputFile: String,
    val generator: GeneratorType,
    val outputDir: String,
    val outputContract: Boolean,
    val sourcePackage: String,
    val modelPrefix: String,
) {

  fun prettyPrint(indent: String = "\t"): String =
      """|contractFile='$contractFile'
         |contractOutputFile='$contractOutputFile'
         |outputDir='$outputDir'
         |outputContract=$outputContract
         |sourcePackage='$sourcePackage'
         |modelPrefix='$modelPrefix'""".trimMargin().prependIndent(indent)
}