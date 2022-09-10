package io.github.ruedigerk.contractfirst.generator.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.github.ruedigerk.contractfirst.generator.ParserFileNotFoundException
import io.github.ruedigerk.contractfirst.generator.ParserIoException
import io.github.ruedigerk.contractfirst.generator.model.Position
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Support functions for parsing OpenAPI and JSON Schema files.
 */
class YamlReader {

  private val objectMapper = ObjectMapper(YAMLFactory())

  fun readFile(file: File): Parseable {
    try {
      val position = Position(file)
      val jsonNode = objectMapper.readTree(file)
      return Parseable(jsonNode, position)
    } catch (e: FileNotFoundException) {
      throw ParserFileNotFoundException("File '$file' not found", e)
    } catch (e: IOException) {
      throw ParserIoException("Error reading file '$file'", e)
    }
  }
}