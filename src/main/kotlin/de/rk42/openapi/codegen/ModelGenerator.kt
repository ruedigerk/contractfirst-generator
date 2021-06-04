package de.rk42.openapi.codegen

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.NameAllocator
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import de.rk42.openapi.codegen.JavaTypes.toTypeName
import de.rk42.openapi.codegen.Names.capitalize
import de.rk42.openapi.codegen.model.java.JavaBuiltIn
import de.rk42.openapi.codegen.model.java.JavaClass
import de.rk42.openapi.codegen.model.java.JavaEnum
import de.rk42.openapi.codegen.model.java.JavaProperty
import de.rk42.openapi.codegen.model.java.JavaSpecification
import de.rk42.openapi.codegen.model.java.JavaType
import java.io.File
import java.util.*
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC

/**
 * Generates the code for the model classes.
 * 
 * TODO: Properly use NameAllocator with scopes, see https://github.com/square/wire/blob/d48be72904d7f6e1458b762cd936b1a7069c2813/wire-java-generator/src/main/java/com/squareup/wire/java/JavaGenerator.java#L1278-L1403
 */
class ModelGenerator(configuration: CliConfiguration) {

  private val outputDir = File(configuration.outputDir)
  private val modelPackage = "${configuration.sourcePackage}.model"

  fun generateCode(specification: JavaSpecification) {
    outputDir.mkdirs()

    specification.typesToGenerate.asSequence()
        .map(::toJavaFile)
        .forEach(this::writeFile)
  }

  private fun writeFile(javaFile: JavaFile) {
    javaFile.writeTo(outputDir)
  }

  private fun toJavaFile(javaType: JavaType): JavaFile {
    val typeSpec = when (javaType) {
      is JavaClass -> toJavaClass(javaType)
      is JavaEnum -> toJavaEnum(javaType)
      is JavaBuiltIn -> throw IllegalArgumentException("Error: trying to generate model class for JavaBuiltIn $javaType")
    }

    return JavaFile.builder(modelPackage, typeSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toJavaClass(javaClass: JavaClass): TypeSpec {
    val fields = javaClass.properties.map(::toField)

    val accessors = javaClass.properties.flatMap { generateAccessorMethods(it, javaClass.className) }
    val equals = generateEquals(javaClass.className.toTypeName(), fields)
    val hashCode = generateHashCode(fields)
    val toString = generateToString(javaClass.className, fields)

    return TypeSpec.classBuilder(javaClass.className)
        .addModifiers(PUBLIC)
        .addFields(fields)
        .addMethods(accessors)
        .addMethod(equals)
        .addMethod(hashCode)
        .addMethod(toString)
        .build()
  }

  private fun generateAccessorMethods(property: JavaProperty, className: String): List<MethodSpec> {
    val propertyTypeName = property.type.toTypeName()

    // The getters is annotated with BeanValidation annotations.
    val getter = generateGetter(property, propertyTypeName)
    val setter = generateSetter(property, propertyTypeName)
    val builder = generateBuilderSetter(property, className, propertyTypeName)

    return listOf(builder, getter, setter)
  }

  private fun generateSetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec =
      MethodSpec.methodBuilder("set${property.javaIdentifier.capitalize()}")
          .addModifiers(PUBLIC)
          .addParameter(propertyTypeName, property.javaIdentifier)
          .addStatement("this.\$1N = \$1N", property.javaIdentifier)
          .build()

  private fun generateBuilderSetter(property: JavaProperty, className: String, propertyTypeName: TypeName): MethodSpec =
      MethodSpec.methodBuilder(property.javaIdentifier)
          .addModifiers(PUBLIC)
          .returns(className.toTypeName())
          .addParameter(propertyTypeName, property.javaIdentifier)
          .addStatement("this.\$1N = \$1N", property.javaIdentifier)
          .addStatement("return this", property.javaIdentifier)
          .build()

  private fun generateGetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec {
    val getterBuilder = MethodSpec.methodBuilder("get${property.javaIdentifier.capitalize()}")
        .addModifiers(PUBLIC)
        .returns(propertyTypeName)
        .addStatement("return \$N", property.javaIdentifier)

    if (property.required) {
      // Required fields are annotated with @NotNull.
      getterBuilder.addAnnotation("javax.validation.constraints.NotNull".toTypeName())
    }
    if (property.type.isGeneratedType) {
      // Fields of generated types (e.g. not java.lang.String) are annotated with @Valid.
      getterBuilder.addAnnotation("javax.validation.Valid".toTypeName())
    }

    return getterBuilder.build()
  }

  private fun toField(property: JavaProperty): FieldSpec {
    return FieldSpec.builder(property.type.toTypeName(), property.javaIdentifier, PRIVATE).build()
  }

  private fun generateEquals(/*nameAllocator: NameAllocator,*/ thisTypeName: TypeName, fields: List<FieldSpec>): MethodSpec {
    // val localNameAllocator = nameAllocator.clone()
    val localNameAllocator = NameAllocator()
    fields.forEach { localNameAllocator.newName(it.name, it) }

    val parameterName = localNameAllocator.newName("other")
    val otherName = localNameAllocator.newName("o")

    val result = MethodSpec.methodBuilder("equals")
        .addAnnotation(Override::class.java)
        .addModifiers(PUBLIC)
        .returns(Boolean::class.javaPrimitiveType)
        .addParameter(Any::class.java, parameterName)

    if (fields.isEmpty()) {
      result.addStatement("return getClass() == \$N.getClass()", parameterName)
      return result.build()
    }

    result.addStatement("if (\$N == this) return true", parameterName)
    result.addStatement("if (\$1N == null || getClass() != \$1N.getClass()) return false", parameterName)
    result.addStatement("\$T \$N = (\$T) \$N", thisTypeName, otherName, thisTypeName, parameterName)

    val firstFieldName = localNameAllocator[fields[0]]
    result.addCode("\$[return $1T.equals($2L, $3N.$2L)", Objects::class.java, firstFieldName, otherName)

    for (index in 1..fields.lastIndex) {
      val field = fields[index]
      val fieldName = localNameAllocator[field]
      result.addCode("\n&& $1T.equals($2L, $3N.$2L)", Objects::class.java, fieldName, otherName)
    }

    result.addCode(";\n$]")

    return result.build()
  }

  private fun generateHashCode(/*nameAllocator: NameAllocator,*/ fields: List<FieldSpec>): MethodSpec? {
    // val localNameAllocator = nameAllocator.clone()
    val localNameAllocator = NameAllocator()
    fields.forEach { localNameAllocator.newName(it.name, it) }

    val result = MethodSpec.methodBuilder("hashCode")
        .addAnnotation(Override::class.java)
        .addModifiers(PUBLIC)
        .returns(Int::class.javaPrimitiveType)

    if (fields.isEmpty()) {
      result.addStatement("return 0")
      return result.build()
    }

    result.addCode("\$[return Objects.hash(")

    for (index in 0 until fields.lastIndex) {
      val field = fields[index]
      result.addCode("\$N, ", field)
    }

    result.addCode("\$N", fields.last())
    result.addCode(");\n\$]")

    return result.build()
  }

  /**
   * TODO: Generate more fancy indented toString like OpenAPI-Generator, example:
   *
   *  public String toString() {
   *    StringBuilder sb = new StringBuilder();
   *    sb.append("class Pet {\n");
   *  
   *    sb.append("    id: ").append(toIndentedString(id)).append("\n");
   *    sb.append("    name: ").append(toIndentedString(name)).append("\n");
   *    sb.append("    tag: ").append(toIndentedString(tag)).append("\n");
   *    sb.append("}");
   *    return sb.toString();
   *  }
   *
   *  private String toIndentedString(Object o) {
   *    if (o == null) {
   *      return "null";
   *    }
   *    return o.toString().replace("\n", "\n    ");
   *  }
   */
  private fun generateToString(/*nameAllocator: NameAllocator,*/thisTypeName: String, fields: List<FieldSpec>): MethodSpec? {
    // val localNameAllocator = nameAllocator.clone()
    val nameAllocator = NameAllocator()
    fields.forEach { nameAllocator.newName(it.name, it) }

    val result = MethodSpec.methodBuilder("toString")
        .addAnnotation(Override::class.java)
        .addModifiers(PUBLIC)
        .returns(String::class.java)

    val builderName = nameAllocator.newName("builder")
    result.addStatement("$1T $2N = new $1T()", StringBuilder::class.java, builderName)

    for (field in fields) {
      val fieldName = nameAllocator[field]
      result.addStatement("\$N.append(\", \$N=\").append(\$L)", builderName, field.name, fieldName)
    }

    result.addStatement("return builder.replace(0, 2, \"\$L{\").append('}').toString()", thisTypeName)

    return result.build()
  }

  private fun toJavaEnum(enum: JavaEnum): TypeSpec {
    TODO("Not yet implemented")
  }
}