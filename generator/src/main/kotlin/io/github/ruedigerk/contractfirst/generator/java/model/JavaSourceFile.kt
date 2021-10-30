package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents a Java source file, i.e. the data necessary to generate a Java class or enum.
 */
sealed interface JavaSourceFile

/**
 * Represents the data necessary to generate a Java class source file.
 */
data class JavaClassFile(
    val className: String,
    val javadoc: String?,
    val properties: List<JavaProperty>,
) : JavaSourceFile

/**
 * Represents the a property of a Java class, i.e. a field with its getters and setters.
 */
data class JavaProperty(
    
    /**
     * The name for the property to use in Java code. May not equal the original property name.
     */
    val javaName: String,
    val javadoc: String?,

    /**
     * The original name of the property in the contract. May not be a valid Java identifier.
     */
    val originalName: String,
    val required: Boolean,
    val type: JavaAnyType,
    val initializerType: JavaAnyType?
)

/**
 * Represents the data necessary to generate a Java enum source file.
 */
data class JavaEnumFile(
    val className: String,
    val javadoc: String?,
    val constants: List<EnumConstant>
) : JavaSourceFile

/**
 * Represents an enum constant.
 */
data class EnumConstant(

    /**
     * The name for the enum constant to use in Java code. May not equal the original enum constant name.
     */
    val javaName: String,

    /**
     * The original name of the enum constant in the contract. May not be a valid Java identifier.
     */
    val originalName: String
)
