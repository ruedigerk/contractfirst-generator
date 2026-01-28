package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.ParserFileNotFoundException
import io.github.ruedigerk.contractfirst.generator.openapi.Position
import java.io.File

/**
 * Reads files and caches their content.
 */
class ParseableCache {

  private val yamlReader = YamlReader()
  private val fileCache: MutableMap<File, Parseable> = mutableMapOf()
  private val positionCache: MutableMap<Position, Parseable> = mutableMapOf()

  fun get(path: String): Parseable = get(Position(path))

  fun get(file: File): Parseable = get(Position(file))

  fun get(position: Position): Parseable = positionCache.getOrPut(position) {
    val file = lookupFile(position.file)
    var target = file

    for (field in position.path) {
      target = target.requiredField(field)
    }

    target
  }

  private fun lookupFile(file: File): Parseable = fileCache.getOrPut(file) {
    yamlReader.readFile(file)
  }

  /**
   * Recursively dereferences the supplied parseable if it is a reference, else returns the supplied parseable.
   */
  fun resolveWhileReference(parseable: Parseable): Parseable = if (parseable.isReference()) resolveReferenceChain(parseable) else parseable
  
  /**
   * Resolves the parseable that is referenced by the supplied parseable. If the referenced parseable is itself a reference, repeat the process until a
   * non-reference parseable is found or a cycle is detected.
   */
  private fun resolveReferenceChain(originParseable: Parseable): Parseable {
    val visitedPositions = mutableSetOf<Position>()
    var currentParseable = originParseable

    do {
      if (!visitedPositions.add(currentParseable.position)) {
        throw ParserContentException("Cyclic references detected at ${currentParseable.position}, starting with ${originParseable.getReference()} at ${originParseable.position}")
      }

      currentParseable = resolveReferenceOnce(currentParseable)
    } while (currentParseable.isReference())
    
    return currentParseable
  }

  private fun resolveReferenceOnce(parseable: Parseable): Parseable {
    val referencedPosition = parseable.resolveReference()

    try {
      return get(referencedPosition)
    } catch (e: ParserFileNotFoundException) {
      throw ParserContentException("Reference '${parseable.getReference()}' is invalid, file '${referencedPosition.file}' does not exist at ${parseable.position}", e)
    }
  }
}
