package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaConstant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.*

/**
 * Transforms the parsed schemas into Java source file representations, appropriate for code generation.
 */
class JavaSchemaToSourceTransformer(
    private val schemas: Map<SchemaId, Schema>,
    private val types: Map<SchemaId, JavaAnyType>
) {

  fun transform(): List<JavaSourceFile> = schemas.mapNotNull { (id, schema) -> toJavaSourceFile(id, schema) }

  private fun toJavaSourceFile(id: SchemaId, schema: Schema): JavaSourceFile? = when (schema) {
    is ObjectSchema -> toJavaClassFile(id, schema)
    is EnumSchema -> toJavaEnumFile(id, schema)
    is ArraySchema -> null
    is MapSchema -> null
    is PrimitiveSchema -> null
  }

  private fun toJavaClassFile(id: SchemaId, schema: ObjectSchema): JavaClassFile? {
    val type = types[id]!!
    val className = type.name
    val properties = schema.properties.map(::toJavaProperty)

    // Special case for empty object schemas, that are generated as java.lang.Object, so no class file need to be generated.
    if (properties.isEmpty()) {
      return null
    }

    return JavaClassFile(className, JavadocHelper.toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: SchemaProperty): JavaProperty {
    val type = types[property.schema]!!

    val initializer = when(type.name) {
      JavaTypeName.SET -> JavaType(JavaTypeName.HASH_SET)
      JavaTypeName.LIST -> JavaType(JavaTypeName.ARRAY_LIST)
      JavaTypeName.MAP -> JavaType(JavaTypeName.HASH_MAP)
      else -> null
    }

    return JavaProperty(
        property.name.toJavaIdentifier(),
        JavadocHelper.toJavadoc(schemas[property.schema]!!),
        property.name,
        property.required,
        type,
        initializer
    )
  }

  private fun toJavaEnumFile(id: SchemaId, schema: EnumSchema): JavaEnumFile {
    val type = types[id]!!
    val constants = schema.values.map { EnumConstant(it.toJavaConstant(), it) }

    return JavaEnumFile(type.name, JavadocHelper.toJavadoc(schema), constants)
  }
}

