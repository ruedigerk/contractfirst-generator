package de.rk42.openapi.codegen

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import de.rk42.openapi.codegen.crosscutting.LogbackConfigurator
import de.rk42.openapi.codegen.parser.ParserException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The Command Line Interface for invoking OpenApiCodegen on the command line.
 */
object CommandLineInterface {

  private val log: Logger = LoggerFactory.getLogger(CommandLineInterface::class.java)

  @JvmStatic
  fun main(args: Array<String>) {
    val config = ArgParser(args).parseInto(::CliConfiguration)

    applyLoggingConfiguration(config)

    log.info("Generating code for contract '${config.contractFile}' in output directory '${config.outputDir}', package '${config.sourcePackage}'")

    try {
      OpenApiCodegen.generate(config)
    } catch (e: ParserException) {
      val messages = e.messages.joinToString("\n")
      log.error("Could not parse contract: {}", messages)
    } catch (e: NotSupportedException) {
      log.error("Contract contains unsupported usage: ${e.message}")
    }
  }

  private fun applyLoggingConfiguration(config: CliConfiguration) {
    when {
      config.verbose && config.quiet -> throw InvalidConfigurationException("Options -q (--quiet) and -v (--verbose) must not be used together")
      config.verbose -> LogbackConfigurator.verboseLogLevels()
      config.quiet -> LogbackConfigurator.quietLogLevels()
    }
  }
}

class CliConfiguration(parser: ArgParser) {

  val contractFile: String by parser.storing("--contract", help = "the path to the file containing the OpenAPI contract to use as input")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  val sourcePackage: String by parser.storing("--package", help = "the Java package to put generated classes into")

  val modelPrefix: String by parser.storing("--model-prefix", help = "the prefix for model file names").default("")

  val verbose: Boolean by parser.flagging("--verbose", "-v", help = "verbose output")

  val quiet: Boolean by parser.flagging("--quit", "-q", help = "quiet output")
}

class InvalidConfigurationException(msg: String) : RuntimeException(msg)