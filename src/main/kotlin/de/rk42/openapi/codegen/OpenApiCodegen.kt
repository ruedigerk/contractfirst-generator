package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.java.generator.model.ModelGenerator
import de.rk42.openapi.codegen.java.generator.server.ServerStubGenerator
import de.rk42.openapi.codegen.java.transform.JavaTransformer
import de.rk42.openapi.codegen.parser.Parser

/**
 * Implements the OpenApiCodegen functionality.
 * 
 * TODO:
 *  - Non-string enums
 *  - Only generate enum constants with @SerializedName if necessary.
 *  - Store the "real" value of an enum constant in a "value" field with getter.
 *  - Add JSON-Pointers to exceptions/error messages to pinpoint the error origin.
 *  - Print out the YAML in case of error, to help showing to where the JSON-Pointers point, as swagger-parser inlines external references. 
 *  - Check why uspto contract generates invalid code.
 *  - Support allOf, oneOf (anyOf?).
 */
object OpenApiCodegen {

  fun generate(configuration: CliConfiguration) {
    val ctrSpecification = Parser().parse(configuration.contractFile)
    val javaSpecification = JavaTransformer(configuration).transform(ctrSpecification)
    
    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }
}