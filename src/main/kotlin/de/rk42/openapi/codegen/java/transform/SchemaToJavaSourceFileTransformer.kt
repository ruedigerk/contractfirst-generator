package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.java.Identifiers.toJavaConstant
import de.rk42.openapi.codegen.java.Identifiers.toJavaIdentifier
import de.rk42.openapi.codegen.java.model.EnumConstant
import de.rk42.openapi.codegen.java.model.JavaClassFile
import de.rk42.openapi.codegen.java.model.JavaCollectionType
import de.rk42.openapi.codegen.java.model.JavaEnumFile
import de.rk42.openapi.codegen.java.model.JavaMapType
import de.rk42.openapi.codegen.java.model.JavaProperty
import de.rk42.openapi.codegen.java.model.JavaSourceFile
import de.rk42.openapi.codegen.java.model.JavaType
import de.rk42.openapi.codegen.model.CtrSchemaArray
import de.rk42.openapi.codegen.model.CtrSchemaEnum
import de.rk42.openapi.codegen.model.CtrSchemaMap
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaObject
import de.rk42.openapi.codegen.model.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.CtrSchemaProperty

/**
 * Transforms the parsed schemas into Java source file representations, appropriate for code generation.
 */
class SchemaToJavaSourceFileTransformer(private val typeLookup: JavaTypeLookup) {

  fun transformedJavaModelFiles(): List<JavaSourceFile> = typeLookup.allSchemas.mapNotNull(::toJavaSourceFile)

  private fun toJavaSourceFile(schema: CtrSchemaNonRef): JavaSourceFile? = when (schema) {
    is CtrSchemaObject -> toJavaClassFile(schema)
    is CtrSchemaEnum -> toJavaEnumFile(schema)
    is CtrSchemaArray -> null
    is CtrSchemaMap -> null
    is CtrSchemaPrimitive -> null
  }

  private fun toJavaClassFile(schema: CtrSchemaObject): JavaClassFile {
    val type = typeLookup.lookupJavaTypeFor(schema)
    val className = type.name
    val properties = schema.properties.map(::toJavaProperty)

    return JavaClassFile(className, TransformerHelper.toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")
    val type = typeLookup.lookupJavaTypeFor(schema)

    val initializer = when {
      type is JavaCollectionType && type.name == "Set" -> JavaType("HashSet", "java.util")
      type is JavaCollectionType -> JavaType("ArrayList", "java.util")
      type is JavaMapType -> JavaType("HashMap", "java.util")
      else -> null
    }

    return JavaProperty(
        property.name.toJavaIdentifier(),
        TransformerHelper.toJavadoc(schema),
        property.name,
        property.required,
        type,
        initializer
    )
  }

  private fun toJavaEnumFile(schema: CtrSchemaEnum): JavaEnumFile {
    val type = typeLookup.lookupJavaTypeFor(schema)
    val constants = schema.values.map { EnumConstant(it.toJavaConstant(), it) }

    return JavaEnumFile(type.name, TransformerHelper.toJavadoc(schema), constants)
  }
}
