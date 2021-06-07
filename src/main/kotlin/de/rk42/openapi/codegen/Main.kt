package de.rk42.openapi.codegen

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import de.rk42.openapi.codegen.parser.ParserException

/**
 * The Command Line Interface application for invoking OpenApiCodegen.
 */
fun main(args: Array<String>) {
  val configuration = ArgParser(args).parseInto(::CliConfiguration)

  println("Generating code for contract '${configuration.contractFile}' in output directory '${configuration.outputDir}', package '${configuration.sourcePackage}'")

  try {
    OpenApiCodegen.generate(configuration)
  } catch (e: ParserException) {
    println("Error parsing contract:")
    e.messages.forEach(::println)
  } catch (e: NotSupportedException) {
    println("Error, contract contains unsupported usage: ${e.message}")
  }
}

class CliConfiguration(parser: ArgParser) {

  val contractFile: String by parser.storing("--contract", help = "the path to the file containing the OpenAPI contract to use as input")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  // TODO: Validate package for invalid characters etc.
  val sourcePackage: String by parser.storing("--package", help = "the Java package to put generated classes into")
  
  val modelPrefix: String by parser.storing("--model-prefix", help = "the prefix for model file names").default("")
}
