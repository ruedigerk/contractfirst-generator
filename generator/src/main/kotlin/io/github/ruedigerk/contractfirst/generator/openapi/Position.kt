package io.github.ruedigerk.contractfirst.generator.openapi

import io.github.ruedigerk.contractfirst.generator.ParserContentException
import java.io.File
import java.net.URI
import java.net.URISyntaxException

/**
 * Represents the position during parsing as the combination of the parsed file together with a path to the position in the file.
 */
data class Position private constructor(
    val file: File,
    val path: List<String>
) {

  operator fun plus(additional: String): Position = Position(file, path + additional)

  fun addPathHint(hint: String): Position = Position(file, pathWithHint(hint))

  private fun pathWithHint(hint: String): List<String> {
    return if (path.isEmpty()) {
      listOf(hint)
    } else {
      val newElement = "${path.last()} ($hint)"
      path.dropLast(1) + newElement
    }
  }

  fun resolveReference(reference: String): Position {
    val uri = try {
      URI(reference)
    } catch (e: URISyntaxException) {
      throw ParserContentException("Illegal \$ref, value is not a valid URI in '$reference' at $this ")
    }
    val referencedFile = uri.path?.takeIf { it.isNotEmpty() }?.let { path -> file.parentFile.resolve(mendPath(path)).canonicalFile } ?: file
    val referencedPath = parseFragment(uri.fragment, reference)

    return Position(referencedFile, referencedPath)
  }

  private fun mendPath(path: String): String = if (path.startsWith("./")) path.drop(2) else path

  private fun parseFragment(fragment: String?, reference: String): List<String> {
    if (fragment == null) {
      return emptyList()
    }
    if (fragment.isEmpty() || !fragment.startsWith('/')) {
      throw ParserContentException("Illegal JSON Pointer, does not start with a slash in \$ref '$reference' at $this ")
    }
    return fragment.drop(1).split('/')
  }

  override fun toString(): String {
    return if (path.isEmpty()) {
      file.path
    } else {
      "${file.path}#$path"
    }
  }

  companion object {

    operator fun invoke(path: String) = Position(File(path))

    operator fun invoke(file: File) = Position(file.canonicalFile, emptyList())
  }
}
