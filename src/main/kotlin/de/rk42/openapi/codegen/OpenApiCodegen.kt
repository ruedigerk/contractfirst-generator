package de.rk42.openapi.codegen

/**
 * Implements the OpenApiCodegen functionality.
 */
object OpenApiCodegen {

  fun generate(configuration: CliConfiguration) {
    val ctrSpecification = Parser().parse(configuration.contractFile)
    val javaSpecification = JavaTransformer().transform(ctrSpecification)
    
    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }
}