package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.java.generator.model.ModelGenerator
import de.rk42.openapi.codegen.java.generator.server.ServerStubGenerator
import de.rk42.openapi.codegen.java.transform.JavaTransformer
import de.rk42.openapi.codegen.parser.Parser

/**
 * Implements the OpenApiCodegen functionality.
 * 
 * TODO:
 *  - Support inline schemas, e.g. SiCo inline enums.
 *  - Check why uspto contract generates invalid code.
 *  - Support allOf, oneOf (anyOf?)
 */
object OpenApiCodegen {

  fun generate(configuration: CliConfiguration) {
    val ctrSpecification = Parser().parse(configuration.contractFile)
    val javaSpecification = JavaTransformer(configuration).transform(ctrSpecification)
    
    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }
}