package de.rk42.openapi.codegen.model.java

data class JavaSpecification(
    val operationGroups: List<JavaOperationGroup>,
    val typesToGenerate: List<JavaType>
)
