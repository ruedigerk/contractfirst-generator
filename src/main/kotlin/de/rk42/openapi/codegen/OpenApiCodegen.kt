package de.rk42.openapi.codegen

/**
 * Implements the OpenApiCodegen functionality.
 */
object OpenApiCodegen {

  fun generate(configuration: CliConfiguration) {
    val ctrSpecification = Parser.parse(configuration.contractFile)
    val itmSpecification = ContractToIntermediateModelTransformer().transform(ctrSpecification)
    val javaSpecification = IntermediateToJavaModelTransformer().transform(itmSpecification)
    
    println("Paths of spec: ${ctrSpecification.pathItems.joinToString()}")
    
    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }
}