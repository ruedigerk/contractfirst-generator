package de.rk42.openapi.codegen.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import de.rk42.openapi.codegen.*
import de.rk42.openapi.codegen.logging.Log
import de.rk42.openapi.codegen.logging.Slf4jLogAdapter
import de.rk42.openapi.codegen.parser.ParserException
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * The Command Line Interface for invoking OpenApiCodegen on the command line.
 */
object CommandLineInterface {

  private val logAdapter = Slf4jLogAdapter(LoggerFactory.getLogger("OpenAPI-Codegen"))
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
      OpenApiCodegen(logAdapter).generate(config)
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
      cliConfiguration.outputDir,
      cliConfiguration.outputContract,
      cliConfiguration.sourcePackage,
      cliConfiguration.modelPrefix,
  )

  private fun toLoggingVerbosity(config: CliConfiguration): LoggingVerbosity = when {
    config.verbose -> LoggingVerbosity.VERBOSE
    config.quiet -> LoggingVerbosity.QUIET
    else -> LoggingVerbosity.NORMAL
  }
}

private class CliConfiguration(parser: ArgParser) {

  val contractFile: String by parser.storing("--contract", help = "the path to the file containing the OpenAPI contract to use as input")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  val sourcePackage: String by parser.storing("--package", help = "the Java package to put generated classes into")

  val modelPrefix: String by parser.storing("--model-prefix", help = "the prefix for model file names").default("")

  val verbose: Boolean by parser.flagging("--verbose", "-v", help = "verbose output")

  val quiet: Boolean by parser.flagging("--quiet", "-q", help = "quiet output")

  val outputContract: Boolean by parser.storing(
      "--output-contract",
      help = "whether to output the parsed contract as an all-in-one contract"
  ) { toBoolean() }.default(true)

  val contractOutputFile: String by parser.storing(
      "--contract-output-file",
      help = "the location to output the 'all in one' contract file to"
  ).default("openapi.yaml")

  init {
    if (verbose && quiet) {
      throw InvalidConfigurationException("Options -q (--quiet) and -v (--verbose) must not be used together")
    }
  }
}

private class InvalidConfigurationException(msg: String) : RuntimeException(msg)
