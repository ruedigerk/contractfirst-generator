package org.contractfirst.generator.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import org.contractfirst.generator.Configuration
import org.contractfirst.generator.ContractfirstGenerator
import org.contractfirst.generator.GeneratorType
import org.contractfirst.generator.NotSupportedException
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.logging.Slf4jLogAdapter
import org.contractfirst.generator.parser.ParserException
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

    log.info { "Generating code for contract '${cliConfig.contractFile}' in output directory '${cliConfig.outputDir}', package '${cliConfig.sourcePackage}'" }

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
    }
  }

  private fun exit(errorCode: Int, msg: () -> String): Nothing {
    log.error(msg)
    exitProcess(errorCode)
  }

  private fun mapToConfiguration(cliConfiguration: CliConfiguration) = Configuration(
      cliConfiguration.contractFile,
      cliConfiguration.contractOutputFile,
      determineGenerator(cliConfiguration.generator),
      cliConfiguration.outputDir,
      cliConfiguration.outputContract,
      cliConfiguration.sourcePackage,
      cliConfiguration.modelPrefix,
  )

  private fun determineGenerator(generator: String): GeneratorType = when (generator) {
    "client" -> GeneratorType.CLIENT
    "server" -> GeneratorType.SERVER
    else -> throw InvalidConfigurationException("Option --generator has invalid value: '$generator', allowed values are 'client', 'server'")
  }

  private fun toLoggingVerbosity(config: CliConfiguration): LoggingVerbosity = when {
    config.verbose -> LoggingVerbosity.VERBOSE
    config.quiet -> LoggingVerbosity.QUIET
    else -> LoggingVerbosity.NORMAL
  }
}

private class CliConfiguration(parser: ArgParser) {

  val contractFile: String by parser.storing("--contract", help = "the path to the file containing the OpenAPI contract to use as input")

  val contractOutputFile: String by parser.storing(
      "--contract-output-file",
      help = "the location to output the 'all in one' contract file to"
  ).default("openapi.yaml")

  val generator: String by parser.storing("--generator", help = "the type of generator to use for code generation; allowed values are: \"server\", \"client\"")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  val sourcePackage: String by parser.storing("--package", help = "the Java package to put generated classes into")

  val modelPrefix: String by parser.storing("--model-prefix", help = "the prefix for model file names").default("")

  val verbose: Boolean by parser.flagging("--verbose", "-v", help = "verbose output")

  val quiet: Boolean by parser.flagging("--quiet", "-q", help = "quiet output")

  val outputContract: Boolean by parser.storing(
      "--output-contract",
      help = "whether to output the parsed contract as an all-in-one contract"
  ) { toBoolean() }.default(true)

  init {
    if (verbose && quiet) {
      throw InvalidConfigurationException("Options -q (--quiet) and -v (--verbose) must not be used together")
    }
  }
}

private class InvalidConfigurationException(msg: String) : RuntimeException(msg)
