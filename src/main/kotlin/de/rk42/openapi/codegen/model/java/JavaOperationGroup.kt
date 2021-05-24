package de.rk42.openapi.codegen.model.java

data class JavaOperationGroup(
    val javaIdentifier: String,
    val operations: List<JavaOperation>
)
