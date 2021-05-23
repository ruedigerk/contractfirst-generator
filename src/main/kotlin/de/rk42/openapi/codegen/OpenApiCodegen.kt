package de.rk42.openapi.codegen

/**
 * Implements the OpenApiCodegen functionality.
 */
object OpenApiCodegen {

  fun generate(configuration: Configuration) {
    val contract = Parser.parse(configuration.contractFile.toString())
    val codeUnits = ModelTransformer.transform(contract)
    
    println("Paths of spec: ${contract.paths.joinToString()}")
    
    ServerStubGenerator(configuration).generateCode(codeUnits)
    ModelGenerator.generateCode(codeUnits, configuration)
  }
}