package io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.configuration.ModelVariant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.jsr305NullabilityAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.MethodsFromObject
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.generator.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.JavaClassFile
import io.github.ruedigerk.contractfirst.generator.java.model.JavaEnumFile
import io.github.ruedigerk.contractfirst.generator.java.model.JavaProperty
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSourceFile
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import java.io.File
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC

/**
 * Generates the Java code for the model classes of an API.
 */
class ModelGenerator(configuration: JavaConfiguration) {

  private val outputDir = File(configuration.outputDir)
  private val useJsr305Nullability = configuration.useJsr305NullabilityAnnotations
  private val variant = selectVariant(configuration.modelVariant)

  private fun selectVariant(modelVariant: ModelVariant): ModelGeneratorVariant = when (modelVariant) {
    ModelVariant.GSON -> GsonModelGeneratorVariant()
    ModelVariant.JACKSON -> JacksonModelGeneratorVariant()
  }

  fun generateCode(javaSourceFiles: List<JavaSourceFile>) {
    javaSourceFiles.asSequence()
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

    return JavaFile.builder(sourceFile.typeName.packageName, typeSpec)
      .skipJavaLangImports(true)
      .build()
  }

  private fun toJavaClass(classFile: JavaClassFile): TypeSpec {
    val fields = classFile.properties.map(::toField)
    val accessors = classFile.properties.flatMap { generateAccessorMethods(it, classFile.typeName) }
    val equalsHashCodeAndToString = MethodsFromObject.generateEqualsHashCodeAndToString(classFile.typeName.toClassName(), fields)

    return TypeSpec.classBuilder(classFile.typeName.toClassName())
      .doIfNotNull(classFile.javadoc) { addJavadoc("\$L", it) }
      .addModifiers(PUBLIC)
      .addFields(fields)
      .addMethods(accessors)
      .addMethods(equalsHashCodeAndToString)
      .build()
  }

  private fun generateAccessorMethods(property: JavaProperty, declaringTypeName: JavaTypeName): List<MethodSpec> {
    val propertyTypeName = property.type.toTypeName()

    val getter = generateGetter(property, propertyTypeName)
    val setter = generateSetter(property, propertyTypeName)
    val builder = generateBuilderSetter(property, declaringTypeName, propertyTypeName)

    return listOf(builder, getter, setter)
  }

  private fun generateSetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec =
    MethodSpec.methodBuilder("set${property.javaName.capitalize()}")
      .addModifiers(PUBLIC)
      .addParameter(toSetterParameterSpec(propertyTypeName, property))
      .addStatement("this.\$1N = \$1N", property.javaName)
      .build()

  private fun generateBuilderSetter(property: JavaProperty, declaringTypeName: JavaTypeName, propertyTypeName: TypeName): MethodSpec =
    MethodSpec.methodBuilder(property.javaName)
      .addModifiers(PUBLIC)
      .returns(declaringTypeName.toClassName())
      .addParameter(toSetterParameterSpec(propertyTypeName, property))
      .addStatement("this.\$1N = \$1N", property.javaName)
      .addStatement("return this", property.javaName)
      .build()

  private fun toSetterParameterSpec(propertyTypeName: TypeName, property: JavaProperty): ParameterSpec =
    ParameterSpec.builder(propertyTypeName, property.javaName)
      .doIf(useJsr305Nullability) { addAnnotation(jsr305NullabilityAnnotation(property.required)) }
      .build()

  private fun generateGetter(property: JavaProperty, propertyTypeName: TypeName): MethodSpec {
    return MethodSpec.methodBuilder("get${property.javaName.capitalize()}")
      .doIf(useJsr305Nullability) { addAnnotation(jsr305NullabilityAnnotation(property.required)) }
      .addModifiers(PUBLIC)
      .returns(propertyTypeName)
      .addStatement("return \$N", property.javaName)
      .build()
  }

  private fun toField(property: JavaProperty): FieldSpec {
    val typeValidationAnnotations = property.type.validations.map(Annotations::toAnnotation)

    return FieldSpec.builder(property.type.toTypeName(true), property.javaName, PRIVATE)
      .doIfNotNull(property.javadoc) { addJavadoc("\$L", it) }
      .doIf(property.required) { addAnnotation(Annotations.NOT_NULL_ANNOTATION) }
      .doIf(property.javaName != property.originalName) { addAnnotation(variant.serializedNameAnnotation(property.originalName)) }
      .addAnnotations(typeValidationAnnotations)
      .doIfNotNull(property.initializerType) { initializer("new \$T<>()", it.toTypeName()) }
      .build()
  }

  private fun toJavaEnum(enumFile: JavaEnumFile): TypeSpec {
    // If any of the enum constants has a name, that is not equal to its java name, then generate a "complex" enum, where the toString method returns the
    // original name, so that enums are properly serialized for x-www-form-urlencoded request bodies.
    return if (enumFile.constants.any { it.originalName != it.javaName }) {
      toComplexEnum(enumFile)
    } else {
      toSimpleEnum(enumFile)
    }
  }

  /**
   * Construct a "simple" enum, where the constants' names match their Java names.
   */
  private fun toSimpleEnum(enumFile: JavaEnumFile): TypeSpec {
    val builder = TypeSpec.enumBuilder(enumFile.typeName.toClassName())
      .doIfNotNull(enumFile.javadoc) { addJavadoc("\$L", it) }
      .addModifiers(PUBLIC)

    enumFile.constants.forEach { enumConstant ->
      builder.addEnumConstant(enumConstant.javaName)
    }

    return builder.build()
  }

  /**
   * Construct a "complex" enum, where the constants' names do not match their Java names.
   */
  private fun toComplexEnum(enumFile: JavaEnumFile): TypeSpec {
    val constructorSpec = MethodSpec.constructorBuilder()
      .addParameter(String::class.java, "serializedName")
      .addStatement("this.serializedName = serializedName")
      .build()

    val toStringMethodSpec = MethodSpec.methodBuilder("toString")
      .addAnnotation(Override::class.java)
      .addModifiers(PUBLIC)
      .returns(String::class.java)
      .addStatement("return serializedName")
      .build()

    val builder = TypeSpec.enumBuilder(enumFile.typeName.toClassName())
      .doIfNotNull(enumFile.javadoc) { addJavadoc("\$L", it) }
      .addModifiers(PUBLIC)
      .addField(String::class.java, "serializedName", PRIVATE, FINAL)
      .addMethod(constructorSpec)
      .addMethod(toStringMethodSpec)

    enumFile.constants.forEach { enumConstant ->
      val constant = TypeSpec.anonymousClassBuilder("\$S", enumConstant.originalName)
        .doIf(enumConstant.javaName != enumConstant.originalName) { addAnnotation(variant.serializedNameAnnotation(enumConstant.originalName)) }
        .build()

      builder.addEnumConstant(enumConstant.javaName, constant)
    }

    return builder.build()
  }
}
