/*
 * Copyright (C) 2022 Sopra Financial Technology GmbH
 * Frankenstraße 146, 90461 Nürnberg, Germany
 *
 * This software is the confidential and proprietary information of
 * Sopra Financial Technology GmbH ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * Sopra Financial Technology GmbH.
 */

package io.github.ruedigerk.contractfirst.generator.parser

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

  /**
   * Parses the supplied contract text and returns its result.
   */
  fun parseString(specText: String): OpenAPI {
    val result = OpenAPIParser().readContents(specText, null, parseOptions())
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