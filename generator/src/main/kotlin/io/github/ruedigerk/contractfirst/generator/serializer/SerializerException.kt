package io.github.ruedigerk.contractfirst.generator.serializer

import com.fasterxml.jackson.core.JsonProcessingException

/**
 * Thrown when the OpenAPI contract cannot be serialized to YAML.
 */
class SerializerException(msg: String, e: JsonProcessingException) : RuntimeException(msg, e)