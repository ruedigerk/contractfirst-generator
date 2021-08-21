package de.rk42.openapi.codegen

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import de.rk42.openapi.codegen.crosscutting.Log.Companion.getLogger
import de.rk42.openapi.codegen.parser.ParserException
import kotlin.system.exitProcess

/**
 * The Command Line Interface for invoking OpenApiCodegen on the command line.
 */
object CommandLineInterface {

  private val log = getLogger()

  @JvmStatic
  fun main(args: Array<String>) {
    val config = readConfiguration(args)
    OpenApiCodegen.applyLoggingVerbosity(config.verbosity)

    log.info { "Generating code for contract '${config.contractFile}' in output directory '${config.outputDir}', package '${config.sourcePackage}'" }

    generate(config)
  }

  private fun readConfiguration(args: Array<String>): Configuration {
    return try {
      val cliConfiguration = ArgParser(args).parseInto(::CliConfiguration)
      mapToConfiguration(cliConfiguration)
    } catch (e: InvalidConfigurationException) {
      exit(1) { "Parameters invalid: ${e.message}" }
    }
  }

  private fun generate(config: Configuration) {
    try {
      OpenApiCodegen.generate(config)
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
      toLoggingVerbosity(cliConfiguration)
  )

  private fun toLoggingVerbosity(config: CliConfiguration): Configuration.Verbosity = when {
    config.verbose -> Configuration.Verbosity.VERBOSE
    config.quiet -> Configuration.Verbosity.QUIET
    else -> Configuration.Verbosity.NORMAL
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
