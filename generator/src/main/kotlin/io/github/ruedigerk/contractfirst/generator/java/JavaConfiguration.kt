/*
 * Copyright (C) 2022 Sopra Financial Technology GmbH
 * Frankenstraße 146, 90461 Nürnberg, Germany
 *
 * This software is the confidential and proprietary information of
 * Sopra Financial Technology GmbH ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * Sopra Financial Technology GmbH.
 */

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