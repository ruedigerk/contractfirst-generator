package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaConstant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.*

/**
 * Transforms the parsed schemas into Java source file representations, appropriate for code generation.
 */
class SchemaToJavaSourceFileTransformer(private val typeLookup: JavaTypeLookup) {

  fun transformedJavaModelFiles(): List<JavaSourceFile> = typeLookup.allSchemas.mapNotNull(::toJavaSourceFile)

  private fun toJavaSourceFile(schema: ActualSchema): JavaSourceFile? = when (schema) {
    is ObjectSchema -> toJavaClassFile(schema)
    is EnumSchema -> toJavaEnumFile(schema)
    is ArraySchema -> null
    is MapSchema -> null
    is PrimitiveSchema -> null
  }

  private fun toJavaClassFile(schema: ObjectSchema): JavaClassFile? {
    val type = typeLookup.lookupJavaTypeFor(schema)
    val className = type.name
    val properties = schema.properties.map(::toJavaProperty)

    // Special case for empty object schemas, the are generated as java.lang.Object, so no class file need to be generated.
    if (properties.isEmpty()) {
      return null
    }

    return JavaClassFile(className, JavadocHelper.toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: SchemaProperty): JavaProperty {
    val type = typeLookup.lookupJavaTypeFor(property.schema)

    val initializer = when {
      type is JavaCollectionType && type.name == "Set" -> JavaType("HashSet", "java.util")
      type is JavaCollectionType -> JavaType("ArrayList", "java.util")
      type is JavaMapType -> JavaType("HashMap", "java.util")
      else -> null
    }

    return JavaProperty(
        property.name.toJavaIdentifier(),
        JavadocHelper.toJavadoc(typeLookup.lookupIfRef(property.schema)),
        property.name,
        property.required,
        type,
        initializer
    )
  }

  private fun toJavaEnumFile(schema: EnumSchema): JavaEnumFile {
    val type = typeLookup.lookupJavaTypeFor(schema)
    val constants = schema.values.map { EnumConstant(it.toJavaConstant(), it) }

    return JavaEnumFile(type.name, JavadocHelper.toJavadoc(schema), constants)
  }
}

