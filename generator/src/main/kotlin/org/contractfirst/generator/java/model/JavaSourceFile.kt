package org.contractfirst.generator.java.model

sealed interface JavaSourceFile

data class JavaClassFile(
    val className: String,
    val javadoc: String?,
    val properties: List<JavaProperty>,
) : JavaSourceFile

data class JavaProperty(
    val javaName: String,
    val javadoc: String?,
    val originalName: String,
    val required: Boolean,
    val type: JavaAnyType,
    val initializerType: JavaAnyType?
)

data class JavaEnumFile(
    val className: String,
    val javadoc: String?,
    val constants: List<EnumConstant>
) : JavaSourceFile

data class EnumConstant(
    val javaName: String,
    val value: String
)
