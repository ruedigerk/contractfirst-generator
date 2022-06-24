package io.github.ruedigerk.contractfirst.generator.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import io.github.ruedigerk.contractfirst.generator.*
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.Slf4jLogAdapter
import org.slf4j.LoggerFactory
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

    log.info { "Generating code for contract '${cliConfig.inputContractFile}' in output directory '${cliConfig.outputDir}', package '${cliConfig.outputJavaBasePackage}'" }

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
      exit(2) { "Could not parse contract: ${e.messages.joinToString("\n")}" }
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

  private fun mapToConfiguration(cliConfiguration: CliConfiguration) = Configuration(
      cliConfiguration.inputContractFile,
      determineGenerator(cliConfiguration.generator),
      cliConfiguration.outputDir,
      cliConfiguration.outputContract,
      cliConfiguration.outputContractFile,
      cliConfiguration.outputJavaBasePackage,
      cliConfiguration.outputJavaModelNamePrefix,
      cliConfiguration.outputJavaModelUseJsr305NullabilityAnnotations
  )

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

  val inputContractFile: String by parser.storing("--input-contract-file", help = "the path to the file containing the OpenAPI contract to use as input; in case of the model-only generator, this should point to a single JSON-Schema file in YAML or JSON format or to a directory, which is recursively searched for JSON-Schema files")

  val generator: String by parser.storing("--generator", help = "the type of generator to use for code generation; allowed values are: \"server\", \"client\", \"model-only\"")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  val outputContract: Boolean by parser.storing(
      "--output-contract",
      help = "whether to output the parsed contract as an all-in-one contract"
  ) { toBoolean() }.default(true)

  val outputContractFile: String by parser.storing(
      "--output-contract-file",
      help = "the location to output the 'all in one' contract file to"
  ).default("openapi.yaml")

  val outputJavaBasePackage: String by parser.storing("--output-java-base-package", help = "the Java package to put generated classes into")

  val outputJavaModelNamePrefix: String by parser.storing("--output-java-model-name-prefix", help = "the prefix for Java model class names").default("")

  val outputJavaModelUseJsr305NullabilityAnnotations: Boolean by parser.flagging("--output-java-model-use-jsr305-nullability-annotations", help = "whether to generate JSR-305 nullability annotations for the getter and setter methods of the model classes")

  val verbose: Boolean by parser.flagging("--verbose", "-v", help = "verbose output")

  val quiet: Boolean by parser.flagging("--quiet", "-q", help = "quiet output")

  init {
    if (verbose && quiet) {
      throw InvalidConfigurationException("Options -q (--quiet) and -v (--verbose) must not be used together")
    }
  }
}

private class InvalidConfigurationException(msg: String) : RuntimeException(msg)
