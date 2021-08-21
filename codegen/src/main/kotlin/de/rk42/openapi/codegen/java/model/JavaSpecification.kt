package de.rk42.openapi.codegen.java.model

data class JavaSpecification(
    val operationGroups: List<JavaOperationGroup>,
    val modelFiles: List<JavaSourceFile>
)
