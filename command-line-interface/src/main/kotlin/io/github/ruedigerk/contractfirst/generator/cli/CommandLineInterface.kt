package io.github.ruedigerk.contractfirst.generator.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import io.github.ruedigerk.contractfirst.generator.*
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.Slf4jLogAdapter
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

/**
 * The Command Line Interface for invoking Contractfirst-Generator on the command line.
 */
object CommandLineInterface {

  private val logAdapter = Slf4jLogAdapter(LoggerFactory.getLogger("Contractfirst-Generator"))
  private val log = Log(logAdapter)

  @JvmStatic
  fun main(args: Array<String>) {
    val cliConfig = readConfiguration(args)

    val verbosity = toLoggingVerbosity(cliConfig)
    LogbackConfigurator.applyLoggingVerbosity(verbosity)

    log.info {
      "Generating code for contract '${cliConfig.inputContractFile}' in output directory '${cliConfig.outputDir}', package '${cliConfig.outputJavaBasePackage}'"
    }

    val generatorConfig = mapToConfiguration(cliConfig)
    generate(generatorConfig)
  }

  private fun readConfiguration(args: Array<String>): CliConfiguration {
    return try {
      ArgParser(args).parseInto(::CliConfiguration)
    } catch (e: InvalidConfigurationException) {
      exit(1) { "Parameters invalid: ${e.message}" }
    }
  }

  private fun generate(config: Configuration) {
    try {
      ContractfirstGenerator(logAdapter).generate(config)
    } catch (e: ParserException) {
      exit(2) { "Could not parse contract: ${e.message}" }
    } catch (e: NotSupportedException) {
      exit(3) { "Contract contains unsupported usage: ${e.message}" }
    } catch (e: InvalidConfigurationException) {
      exit(4) { "Invalid configuration ${e.message}" }
    }
  }

  private fun exit(errorCode: Int, msg: () -> String): Nothing {
    log.error(msg)
    exitProcess(errorCode)
  }

  private fun mapToConfiguration(cliConfiguration: CliConfiguration): Configuration {
    val effectiveInputContractFile = determineInputContractFile(cliConfiguration.inputContractFile)
    val effectiveOutputJavaPackageSchemaDirectoryPrefix = determineOutputJavaPackageSchemaDirectoryPrefix(
      effectiveInputContractFile,
      cliConfiguration.outputJavaPackageSchemaDirectoryPrefix,
    )

    return Configuration(
      effectiveInputContractFile,
      determineGenerator(cliConfiguration.generator),
      cliConfiguration.outputDir,
      cliConfiguration.outputContract,
      cliConfiguration.outputContractFile,
      cliConfiguration.outputJavaBasePackage,
      cliConfiguration.outputJavaPackageMirrorsSchemaDirectory,
      effectiveOutputJavaPackageSchemaDirectoryPrefix,
      cliConfiguration.outputJavaModelNamePrefix,
      cliConfiguration.outputJavaModelUseJsr305NullabilityAnnotations,
    )
  }

  private fun determineInputContractFile(inputContractFile: String): String = File(inputContractFile).canonicalPath

  private fun determineOutputJavaPackageSchemaDirectoryPrefix(inputContractFile: String, outputJavaPackageSchemaDirectoryPrefix: String?): String =
    outputJavaPackageSchemaDirectoryPrefix?.let { File(it).absolutePath }
      ?: File(inputContractFile).let { if (it.isDirectory) it.path else it.parent }

  private fun determineGenerator(generator: String): GeneratorType = when (generator) {
    "client" -> GeneratorType.CLIENT
    "server" -> GeneratorType.SERVER
    "model-only" -> GeneratorType.MODEL_ONLY
    else -> throw InvalidConfigurationException("Option --generator has invalid value: '$generator', allowed values are 'client', 'server'")
  }

  private fun toLoggingVerbosity(config: CliConfiguration): LoggingVerbosity = when {
    config.verbose -> LoggingVerbosity.VERBOSE
    config.quiet -> LoggingVerbosity.QUIET
    else -> LoggingVerbosity.NORMAL
  }
}

private class CliConfiguration(parser: ArgParser) {

  val inputContractFile: String by parser.storing(
    "--input-contract-file",
    help = "the path to the file containing the OpenAPI contract to use as input; in case of the model-only generator, this should point to a single JSON-Schema file in YAML or JSON format or to a directory, which is recursively searched for JSON-Schema files",
  )

  val generator: String by parser.storing(
    "--generator",
    help = "the type of generator to use for code generation; allowed values are: \"server\", \"client\", \"model-only\"",
  )

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  val outputContract: Boolean by parser.flagging(
    "--output-contract",
    help = "whether to output the parsed contract as an all-in-one contract",
  ).default(true)

  val outputContractFile: String by parser.storing("--output-contract-file", help = "the location to output the 'all in one' contract file to")
    .default("openapi.yaml")

  val outputJavaBasePackage: String by parser.storing("--output-java-base-package", help = "the Java package to put generated classes into")

  val outputJavaPackageMirrorsSchemaDirectory: Boolean by parser.flagging(
    "--output-java-package-mirrors-schema-directory",
    help = "whether the Java packages of the generated model files are mirroring the directory structure of the schema files",
  ).default(true)

  val outputJavaPackageSchemaDirectoryPrefix: String? by parser.storing(
    "--output-java-package-schema-directory-prefix",
    help = "the path prefix to cut from the schema file directories when determining Java packages for model files; defaults to the directory of the inputContractFile; this is only used, when outputJavaPackageMirrorsSchemaDirectory is true",
  )

  val outputJavaModelNamePrefix: String by parser.storing("--output-java-model-name-prefix", help = "the prefix for Java model class names").default("")

  val outputJavaModelUseJsr305NullabilityAnnotations: Boolean by parser.flagging(
    "--output-java-model-use-jsr305-nullability-annotations",
    help = "whether to generate JSR-305 nullability annotations for the getter and setter methods of the model classes",
  )

  val verbose: Boolean by parser.flagging("--verbose", "-v", help = "verbose output")

  val quiet: Boolean by parser.flagging("--quiet", "-q", help = "quiet output")

  init {
    if (verbose && quiet) {
      throw InvalidConfigurationException("Options -q (--quiet) and -v (--verbose) must not be used together")
    }
  }
}

private class InvalidConfigurationException(msg: String) : RuntimeException(msg)
