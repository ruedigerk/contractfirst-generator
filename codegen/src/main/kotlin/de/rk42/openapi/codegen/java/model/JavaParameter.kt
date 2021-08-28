package de.rk42.openapi.codegen.java.model

import de.rk42.openapi.codegen.model.ParameterLocation

sealed interface JavaParameter {

  val javaIdentifier: String
  val javadoc: String?
  val required: Boolean
  val javaType: JavaAnyType
}

data class JavaRegularParameter(
    override val javaIdentifier: String,
    override val javadoc: String?,
    override val required: Boolean,
    override val javaType: JavaAnyType,
    val location: ParameterLocation,
    val name: String
) : JavaParameter

data class JavaBodyParameter(
    override val javaIdentifier: String,
    override val javadoc: String?,
    override val required: Boolean,
    override val javaType: JavaAnyType,
    val mediaType: String
) : JavaParameter