package de.rk42.openapi.codegen.java.model

data class JavaOperationGroup(
    val javaIdentifier: String,
    val operations: List<JavaOperation>
)
