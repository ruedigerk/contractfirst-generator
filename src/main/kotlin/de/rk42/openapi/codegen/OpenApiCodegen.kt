package de.rk42.openapi.codegen

/**
 * Implements the OpenApiCodegen functionality.
 */
object OpenApiCodegen {

  fun generate(configuration: Configuration) {
    val contract = Parser.parse(configuration.contractFile.toString())
    val codeUnits = ModelTransformer.transform(contract)
    
    ServerStubGenerator.generateCode(codeUnits, configuration)
    ModelGenerator.generateCode(codeUnits, configuration)

    println("Paths of spec: ${contract.paths.joinToString()}")
  }
}