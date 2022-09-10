package io.github.ruedigerk.contractfirst.generator.parser

import com.fasterxml.jackson.databind.JsonNode
import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.model.Position
import java.math.BigDecimal

class Parseable(
    val node: JsonNode?,
    val position: Position
) {
  fun addPositionHint(hint: String): Parseable = Parseable(node, position.addPathHint(hint))

  fun optionalField(fieldName: String): Parseable {
    requireObject()
    return navigate(node?.get(fieldName), fieldName)
  }

  fun requiredField(fieldName: String): Parseable {
    if (node == null) {
      return navigate(null, fieldName)
    }
    
    requireObject()
    val child = node[fieldName]
    return child?.let { navigate(it, fieldName) } ?: throw ParserContentException("$position has no field '$fieldName'")
  }

  fun hasField(fieldName: String): Boolean = node?.has(fieldName) ?: false

  fun isPresent(): Boolean = node != null

  fun isEmpty(): Boolean = node?.isEmpty ?: true

  fun isObject(): Boolean = node?.isObject ?: false

  fun isReference(): Boolean = hasField(DOLLAR_REF)

  fun getReference(): String = requiredField(DOLLAR_REF).string()!!
  
  fun resolveReference(): Position {
    if (!isReference()) {
      throw IllegalStateException("Parseable is not a reference: $this")
    }

    val reference = getReference()
    return position.resolveReference(reference)
  }
  
  fun string(): String? {
    if (node?.isTextual == false) {
      throw ParserContentException("$position is not a string")
    }

    return node?.textValue()
  }

  fun int(): Int? {
    if (node?.isNumber == false) {
      throw ParserContentException("$position is not a number")
    }

    return node?.intValue()
  }

  fun number(): BigDecimal? {
    if (node?.isNumber == false) {
      throw ParserContentException("$position is not a number")
    }

    return node?.decimalValue()
  }

  fun boolean(): Boolean? {
    if (node?.isBoolean == false) {
      throw ParserContentException("$position is not a boolean")
    }
    return node?.booleanValue()
  }

  fun requireObject(): Parseable {
    if (node?.isObject == false) {
      throw ParserContentException("$position is not an object")
    }
    return this
  }

  fun requireArray(): Parseable {
    if (node?.isArray == false) {
      throw ParserContentException("$position is not an array")
    }
    return this
  }

  fun requireNonEmpty(): Parseable {
    if (node?.isEmpty == true) {
      throw ParserContentException("$position is empty")
    }
    return this
  }

  fun fields(): List<Pair<String, Parseable>> {
    requireObject()
    val entries = node?.fields()?.asSequence() ?: emptySequence()
    return entries.map { (name, content) -> Pair(name, navigate(content, name)) }.toList()
  }

  fun elements(): List<Parseable> {
    requireArray()
    return node?.mapIndexed { index, jsonNode -> navigate(jsonNode, "$index") } ?: emptyList()
  }
  
  fun stringElements(): List<String> {
    requireArray()
    val entries = node?.toList() ?: emptyList()

    if (entries.any { it.isObject || it.isArray }) {
      throw ParserContentException("$position contains non-string entries")
    }

    return entries.map { it.asText() }
  }
  
  private fun navigate(jsonNode: JsonNode?, fieldName: String): Parseable = Parseable(jsonNode, position + fieldName)
  
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Parseable

    if (node != other.node) return false
    if (position != other.position) return false

    return true
  }

  override fun hashCode(): Int {
    var result = node?.hashCode() ?: 0
    result = 31 * result + position.hashCode()
    return result
  }

  override fun toString(): String {
    return "Parseable(position=$position, node=$node)"
  }

  companion object {

    private const val DOLLAR_REF = "\$ref"    
  }
}