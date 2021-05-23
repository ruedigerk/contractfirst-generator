package de.rk42.openapi.codegen

import com.xenomachina.argparser.ArgParser

/**
 * The Command Line Interface application for invoking OpenApiCodegen.
 */
fun main(args: Array<String>) {
  val configuration = ArgParser(args).parseInto(::Configuration)

  println("Generating code for contract '${configuration.contractFile}' in output directory '${configuration.outputDir}'")

  try {
    OpenApiCodegen.generate(configuration)
  } catch (e: Parser.ParserException) {
    println("Error parsing input contract:")
    e.messages.forEach(::println)
  }
}

class Configuration(parser: ArgParser) {

  val contractFile: String by parser.storing("--contract", help = "the path to the file containing the OpenAPI contract to use as input")

  val outputDir: String by parser.storing("--output-dir", help = "the path to the directory where the generated code is written to")

  // TODO: Validate package for invalid characters etc.
  val sourcePackage: String by parser.storing("--package", help = "the Java package to put generated classes into")
}
