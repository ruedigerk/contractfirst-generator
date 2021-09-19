package org.contractfirst.generator.java.model

/**
 * Represents any kind of type used in generated Java code. Contains all data necessary to reference a type, including the contrains set on a type via 
 * BeanValidation-Annotations. Does not represent the actual definition of that type. The definiton of a type is JavaSourceFile. 
 */
sealed interface JavaAnyType {

  val name: String
  val packageName: String

  /** Returns whether this is a type that needs to be validated. */
  val validations: List<TypeValidation>
  
  val isGenericType: Boolean
    get() = this !is JavaType
}

/**
 * Represent a non-generic Java type.
 */
data class JavaType(
    override val name: String,
    override val packageName: String,
    override val validations: List<TypeValidation> = emptyList()
) : JavaAnyType {

  override fun toString(): String = "$packageName.$name"
}

/**
 * Represent a collection type, like List and Set.
 */
data class JavaCollectionType(
    override val name: String,
    override val packageName: String,
    val elementType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType {

  override fun toString(): String = "$packageName.$name<$elementType>"
}

/**
 * Represent a collection of type Map.
 */
data class JavaMapType(
    override val name: String,
    override val packageName: String,
    val valuesType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType {

  override fun toString(): String = "$packageName.$name<String, $valuesType>"
}
