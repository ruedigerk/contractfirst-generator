package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.Position
import java.io.File

/**
 * Generates names for schemas that are generated as Java source files.
 */
class JavaTypeNameGenerator(
    private val log: Log,
    private val configuration: JavaConfiguration,
    private val effectiveOperationIds: Map<List<String>, String>
) {

  private val strippedSchemaDirPrefix: String? =
      configuration.outputJavaPackageSchemaDirectoryPrefix.takeIf { configuration.outputJavaPackageMirrorsSchemaDirectory }
          ?.normalizePathSeparators()
          ?.dropLastWhile { it == '/' }

  fun determineName(position: Position): JavaTypeName {
    return nameForContractDefinedType(position) ?: nameSchemaFileDefinedType(position)
  }

  // Assumes the schema is defined in a schema file, not in the contract file
  private fun nameSchemaFileDefinedType(position: Position): JavaTypeName {
    val file = position.file
    val packageName = packageForSchemaFile(file)
    val simpleName = file.nameWithoutExtension.toJavaTypeIdentifier()
    val typeName = typeNameFor(packageName, simpleName)

    return if (position.path.isEmpty()) {
      typeName
    } else {
      nameForNestedType(typeName, position.path)
    }
  }

  private fun typeNameFor(packageName: String, typeName: String) = JavaTypeName(packageName, configuration.modelNamePrefix + typeName)

  private fun packageForSchemaFile(file: File): String {
    if (strippedSchemaDirPrefix == null) {
      return configuration.modelPackage
    }

    val schemaDirectory = file.parent?.normalizePathSeparators()
    val subdirectory = schemaDirectory?.takeIf { it.startsWith(strippedSchemaDirPrefix) }?.drop(strippedSchemaDirPrefix.length)
    val subpackage = subdirectory?.takeIf { it.isNotEmpty() }?.toPackageName()?.let { ".$it" } ?: ""

    if (subdirectory == null) {
      log.warn { "Schema file '$file' does not start with configured prefix '$strippedSchemaDirPrefix'" }
    }

    return configuration.modelPackage + subpackage
  }

  /**
   * Rewrites backslash separators to slashes.
   */
  private fun String.normalizePathSeparators(): String = replace('\\', '/')

  /**
   * Rewrites a path with slash separators to a valid Java package name.
   */
  private fun String.toPackageName(): String = split('/').filter { it.isNotEmpty() }.joinToString(".") { it.toJavaIdentifier() }

  private fun nameForContractDefinedType(position: Position): JavaTypeName? {
    return anyMatchesStart(pathMatchers, position.path)?.let { (matcherName, matchResult) ->
      val typeName = nameForPathCategory(matcherName, matchResult.match)

      return if (matchResult.rest.isEmpty()) {
        typeName
      } else {
        nameForNestedType(typeName, matchResult.rest)
      }
    }
  }

  private fun nameForNestedType(parent: JavaTypeName, rest: List<String>): JavaTypeName {
    var remaining = rest
    val suffix = StringBuilder()

    do {
      val (matcherName, result) = anyMatchesStart(nestedMatchers, remaining)
          ?: throw IllegalArgumentException("Unsupported nesting for type, parent: $parent, nesting: $rest")

      remaining = result.rest

      when (matcherName) {
        "arrayItems" -> suffix.append("Item")
        "objectProperty" -> suffix.append(result.match["propertyName"]!!.toJavaTypeIdentifier())
        else -> throw IllegalArgumentException("Unknown matcherName $matcherName")
      }
    } while (remaining.isNotEmpty())

    return JavaTypeName(parent.packageName, parent.simpleName + suffix)
  }

  private fun anyMatchesStart(matchers: Map<String, PositionPathMatcher>, path: List<String>): Pair<String, PositionPathMatcher.Result>? {
    return matchers.mapValues { (_, matcher) -> matcher.matchesStart(path) }
        .mapNotNull { (matcherName, matchResult) -> matchResult?.let { matcherName to it } }
        .also { if (it.size > 1) throw IllegalStateException("List of match results has multiple entries: $it") }
        .firstOrNull()
  }

  private fun nameForPathCategory(matcherName: String, result: Map<String, String>): JavaTypeName {
    val rawTypeName: String = when (matcherName) {
      "componentSchema" -> result["typeName"]!!
      "pathParameter" -> result["path"] + " Parameter " + result["parameterName"]
      "operationParameter" -> lookupOperationId(result["path"]!!, result["method"]!!) + " Parameter " + result["parameterName"]
      "requestBody" -> lookupOperationId(result["path"]!!, result["method"]!!) + " RequestBody " + result["mediaType"]
      "response" -> lookupOperationId(result["path"]!!, result["method"]!!) + " Response " + result["statusCode"] + " " + result["mediaType"]
      else -> throw IllegalArgumentException("Unknown matcherName $matcherName")
    }
    return typeNameFor(configuration.modelPackage, rawTypeName.toJavaTypeIdentifier())
  }

  private fun lookupOperationId(path: String, method: String): String {
    val pathOfOperation = listOf("paths", path, method)
    return effectiveOperationIds[pathOfOperation] ?: throw IllegalStateException("No operation found with path $pathOfOperation in $effectiveOperationIds")
  }

  companion object {

    private val pathMatchers = mapOf(
        "componentSchema" to PositionPathMatcher.of("components,schemas,<typeName>"),
        "pathParameter" to PositionPathMatcher.of("paths,<path>,parameters,<parameterName>,schema"),
        "operationParameter" to PositionPathMatcher.of("paths,<path>,<method>,parameters,<parameterName>,schema"),
        "requestBody" to PositionPathMatcher.of("paths,<path>,<method>,requestBody,content,<mediaType>,schema"),
        "response" to PositionPathMatcher.of("paths,<path>,<method>,responses,<statusCode>,content,<mediaType>,schema"),
    )

    private val nestedMatchers = mapOf(
        "objectProperty" to PositionPathMatcher.of("properties,<propertyName>"),
        "arrayItems" to PositionPathMatcher.of("items"),
    )
  }
}