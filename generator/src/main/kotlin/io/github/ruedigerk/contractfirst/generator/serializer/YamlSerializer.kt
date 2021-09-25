package io.github.ruedigerk.contractfirst.generator.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature
import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import java.io.IOException

/**
 * Serializes OpenAPI contracts or parts of them to YAML.
 */
object YamlSerializer {

  fun toYaml(value: Any): String {
    val mapper = createMapper()

    if (value is OpenAPI) {
      mapper.registerModule(SimpleModule().apply {
        addSerializer(OpenAPI::class.java, OpenApiSerializer())
      })
    }

    return toYaml(value, mapper)
  }

  private fun toYaml(value: Any, mapper: ObjectMapper): String {
    try {
      return mapper.writeValueAsString(value)
    } catch (e: JsonProcessingException) {
      throw SerializerException("Cannot serialize YAML: ${e.message}", e)
    }
  }

  private fun createMapper(): ObjectMapper {
    return Yaml.mapper().copy().apply {
      enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

      with(factory as YAMLFactory) {
        enable(Feature.MINIMIZE_QUOTES)
        enable(Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        enable(Feature.LITERAL_BLOCK_STYLE)
        disable(Feature.SPLIT_LINES)
      }
    }
  }
}

private class OpenApiSerializer : JsonSerializer<OpenAPI?>() {

  @Throws(IOException::class)
  override fun serialize(value: OpenAPI?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
    if (value == null) {
      return
    }

    jsonGenerator.writeStartObject()
    jsonGenerator.writeStringField("openapi", value.openapi)

    value.info?.let { jsonGenerator.writeObjectField("info", it) }
    value.externalDocs?.let { jsonGenerator.writeObjectField("externalDocs", it) }
    value.servers?.let { jsonGenerator.writeObjectField("servers", it) }
    value.security?.let { jsonGenerator.writeObjectField("security", it) }
    value.tags?.let { jsonGenerator.writeObjectField("tags", it) }
    value.paths?.let { jsonGenerator.writeObjectField("paths", it) }
    value.components?.let { jsonGenerator.writeObjectField("components", it) }

    value.extensions?.let {
      it.forEach { (name, extension) -> jsonGenerator.writeObjectField(name, extension) }
    }

    jsonGenerator.writeEndObject()
  }
}