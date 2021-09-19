package org.contractfirst.generator

/**
 * The configuration for a single run of the application.
 */
data class Configuration(
    val inputContractFile: String,
    val generator: GeneratorType,
    val outputDir: String,
    val outputContract: Boolean,
    val outputContractFile: String,
    val outputJavaBasePackage: String,
    val outputJavaNamePrefix: String,
) {

  fun prettyPrint(indent: String = "\t"): String =
      """|inputContractFile='$inputContractFile'
         |generator='$generator'
         |outputDir='$outputDir'
         |outputContract=outputContract
         |outputContractFile='$outputContractFile'
         |outputJavaBasePackage='$outputJavaBasePackage'
         |outputJavaNamePrefix='$outputJavaNamePrefix'""".trimMargin().prependIndent(indent)
}
