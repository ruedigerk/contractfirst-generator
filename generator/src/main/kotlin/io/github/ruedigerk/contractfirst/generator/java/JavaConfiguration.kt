package io.github.ruedigerk.contractfirst.generator.java

import io.github.ruedigerk.contractfirst.generator.Configuration

/**
 * The configuration for the Java generators.
 */
data class JavaConfiguration(
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