package io.github.ruedigerk.contractfirst.generator.allinonecontract

import io.github.ruedigerk.contractfirst.generator.ParserException
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 * For running Swagger-Parser and validating its result.
 */
class SwaggerParser {

  /**
   * Parses the supplied file and returns its result.
   */
  fun parseFile(specFilePath: String): OpenAPI {
    val result = OpenAPIParser().readLocation(specFilePath, null, parseOptions())
    return validateResult(result)
  }

  private fun parseOptions(): ParseOptions = ParseOptions().apply {
    // Replace remote/relative references with a local references, e.g. "#/components/schemas/NameOfRemoteSchema".
    isResolve = true
  }

  private fun validateResult(result: SwaggerParseResult): OpenAPI {
    if (result.messages.isNotEmpty()) {
      throw ParserException(result.messages)
    }

    return result.openAPI!!
  }
}