package io.github.ruedigerk.contractfirst.generator.java

import io.github.ruedigerk.contractfirst.generator.configuration.Configuration
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant
import io.github.ruedigerk.contractfirst.generator.configuration.ModelVariant

/**
 * The configuration for the Java generators.
 */
data class JavaConfiguration(
    val generatorVariant: GeneratorVariant,
    val modelVariant: ModelVariant,
    val outputDir: String,
    val apiPackage: String,
    val modelPackage: String,
    val supportPackage: String,
    val outputJavaPackageMirrorsSchemaDirectory: Boolean,
    val outputJavaPackageSchemaDirectoryPrefix: String,
    val modelNamePrefix: String,
    val useJsr305NullabilityAnnotations: Boolean,
) {

  companion object {

    fun forFullSpecification(configuration: Configuration, apiPackagePrefix: String) = JavaConfiguration(
        configuration.generatorVariant,
        configuration.modelVariant,
        configuration.outputDir,
        configuration.outputJavaBasePackage + apiPackagePrefix,
        configuration.outputJavaBasePackage + ".model",
        configuration.outputJavaBasePackage + apiPackagePrefix + ".support",
        configuration.outputJavaPackageMirrorsSchemaDirectory,
        configuration.outputJavaPackageSchemaDirectoryPrefix,
        configuration.outputJavaModelNamePrefix,
        configuration.outputJavaModelUseJsr305NullabilityAnnotations,
    )

    fun forModelOnly(configuration: Configuration) = JavaConfiguration(
        configuration.generatorVariant,
        configuration.modelVariant,
        configuration.outputDir,
        configuration.outputJavaBasePackage,
        configuration.outputJavaBasePackage,
        configuration.outputJavaBasePackage,
        configuration.outputJavaPackageMirrorsSchemaDirectory,
        configuration.outputJavaPackageSchemaDirectoryPrefix,
        configuration.outputJavaModelNamePrefix,
        configuration.outputJavaModelUseJsr305NullabilityAnnotations
    )
  }
}
