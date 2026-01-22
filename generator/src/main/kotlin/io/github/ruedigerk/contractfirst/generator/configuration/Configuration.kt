package io.github.ruedigerk.contractfirst.generator.configuration

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier

/**
 * The configuration for a single run of the application.
 */
data class Configuration(
    val inputContractFile: String,
    val generator: GeneratorType,
    val generatorVariant: GeneratorVariant,
    val modelVariant: ModelVariant,
    val outputDir: String,
    val outputContract: Boolean,
    val outputContractFile: String,
    val outputJavaBasePackage: String,
    val outputJavaPackageMirrorsSchemaDirectory: Boolean,
    val outputJavaPackageSchemaDirectoryPrefix: String,
    val outputJavaModelNamePrefix: String,
    val outputJavaModelUseJsr305NullabilityAnnotations: Boolean,
) {

  // TODO: also validate the other configuration parameters
  @Throws(InvalidConfigurationException::class)
  fun validate() {
    if (generator != generatorVariant.associatedGenerator) {
      val validOptions = GeneratorVariant.entries.filter { it.associatedGenerator == generator }
      throw InvalidConfigurationException(
          "parameter generatorVariant: generator variant $generatorVariant is not supported by generator $generator. " +
              "Valid options are: $validOptions"
      )
    }

    if (generatorVariant == GeneratorVariant.CLIENT_OKHTTP && modelVariant != ModelVariant.GSON) {
      throw InvalidConfigurationException("parameter modelVariant: model variant $modelVariant is not supported by OkHttp-Gson client.")
    }

    if (outputJavaModelNamePrefix.isNotEmpty() && outputJavaModelNamePrefix != outputJavaModelNamePrefix.toJavaTypeIdentifier()) {
      throw InvalidConfigurationException(
          "parameter outputJavaModelNamePrefix: \"${outputJavaModelNamePrefix}\" is not a valid prefix for a Java class name, " +
              "e.g. it must start with an upper case letter and must not contain spaces or invalid characters."
      )
    }
  }

  fun prettyPrint(indent: String = "\t"): String =
      """|inputContractFile='$inputContractFile'
         |generator='$generator'
         |generatorVariant='$generatorVariant'
         |modelVariant='$modelVariant'
         |outputDir='$outputDir'
         |outputContract=$outputContract
         |outputContractFile='$outputContractFile'
         |outputJavaBasePackage='$outputJavaBasePackage'
         |outputJavaModelNamePrefix='$outputJavaModelNamePrefix'
         |outputJavaModelUseJsr305NullabilityAnnotations='$outputJavaModelUseJsr305NullabilityAnnotations'""".trimMargin().prependIndent(indent)
}
