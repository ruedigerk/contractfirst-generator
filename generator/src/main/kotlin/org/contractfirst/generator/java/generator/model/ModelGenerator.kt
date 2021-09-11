package org.contractfirst.generator.java.generator.model

import com.squareup.javapoet.*
import org.contractfirst.generator.Configuration
import org.contractfirst.generator.java.Identifiers.capitalize
import org.contractfirst.generator.java.generator.GeneratorCommon
import org.contractfirst.generator.java.generator.GeneratorCommon.NOT_NULL_ANNOTATION
import org.contractfirst.generator.java.generator.GeneratorCommon.toAnnotation
import org.contractfirst.generator.java.generator.GeneratorCommon.toTypeName
import org.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import org.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import org.contractfirst.generator.java.model.*
import java.io.File
import java.util.*
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC

/**
 * Generates the code for the model classes.
 *
 * TODO: Properly use NameAllocator with scopes, see https://github.com/square/wire/blob/d48be72904d7f6e1458b762cd936b1a7069c2813/wire-java-generator/src/main/java/com/squareup/wire/java/JavaGenerator.java#L1278-L1403
 */
class ModelGenerator(configuration: Configuration) {

  private val outputDir = File(configuration.outputDir)
  private val modelPackage = "${configuration.sourcePackage}.model"

  fun generateCode(specification: JavaSpecification) {
    specification.modelFiles.asSequence()
        .map(::toJavaFile)
        .forEach(::writeFile)
  }

  private fun writeFile(javaFile: JavaFile) {
    javaFile.writeTo(outputDir)
  }

  private fun toJavaFile(sourceFile: JavaSourceFile): JavaFile {
    val typeSpec = when (sourceFile) {
      is JavaClassFile -> toJavaClass(sourceFile)
      is JavaEnumFile -> toJavaEnum(sourceFile)
    }

    return JavaFile.builder(modelPackage, typeSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toJavaClass(classFile: JavaClassFile): TypeSpec {
    val fields = classFile.properties.map(::toField)
    val accessors = classFile.properties.flatMap { generateAccessorMethods(it, classFile.className) }
    val equals = generateEquals(classFile.className.toTypeName(), fields)
    val hashCode = generateHashCode(fields)
    val toString = generateToString(classFile.className, fields)

    return TypeSpec.classBuilder(classFile.className)
        .doIfNotNull(classFile.javadoc) { addJavadoc(it) }
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

    // The getter is annotated with BeanValidation annotations.
    val getter = generateGetter(property, propertyTypeName)
    val setter = generateSetter(property, propertyTypeName)
    val builder = generateBuilderSetter(property, className, propertyTypeName)

    return listOf(builder, getter, setter)
  }

  private fun generateSetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec =
      MethodSpec.methodBuilder("set${property.javaName.capitalize()}")
          .addModifiers(PUBLIC)
          .addParameter(propertyTypeName, property.javaName)
          .addStatement("this.\$1N = \$1N", property.javaName)
          .build()

  private fun generateBuilderSetter(property: JavaProperty, className: String, propertyTypeName: TypeName): MethodSpec =
      MethodSpec.methodBuilder(property.javaName)
          .addModifiers(PUBLIC)
          .returns(className.toTypeName())
          .addParameter(propertyTypeName, property.javaName)
          .addStatement("this.\$1N = \$1N", property.javaName)
          .addStatement("return this", property.javaName)
          .build()

  private fun generateGetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec {
    return MethodSpec.methodBuilder("get${property.javaName.capitalize()}")
        .addModifiers(PUBLIC)
        .returns(propertyTypeName)
        .addStatement("return \$N", property.javaName)
        .build()
  }

  private fun toField(property: JavaProperty): FieldSpec {
    val typeValidationAnnotations = property.type.validations.map(GeneratorCommon::toAnnotation)

    return FieldSpec.builder(property.type.toTypeName(), property.javaName, PRIVATE)
        .doIfNotNull(property.javadoc) { addJavadoc(it) }
        .doIf(property.required) { addAnnotation(NOT_NULL_ANNOTATION) }
        .addAnnotations(typeValidationAnnotations)
        .doIfNotNull(property.initializerType) { initializer("new \$T<>()", it.toTypeName()) }
        .build()
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
   * TODO: Generate more fancy indented toString like OpenAPI-Generator? Example:
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

  private fun toJavaEnum(enumFile: JavaEnumFile): TypeSpec {
    val builder = TypeSpec.enumBuilder(enumFile.className)
        .doIfNotNull(enumFile.javadoc) { addJavadoc(it) }
        .addModifiers(PUBLIC)

    enumFile.constants.forEach { enumConstant ->
      val constantBuilder = TypeSpec.anonymousClassBuilder("")

      if (enumConstant.javaName != enumConstant.value) {
        val serializedNameAnnotation = toAnnotation("com.google.gson.annotations.SerializedName", enumConstant.value)
        constantBuilder.addAnnotation(serializedNameAnnotation)
      }

      builder.addEnumConstant(enumConstant.javaName, constantBuilder.build())
    }

    return builder.build()
  }
}