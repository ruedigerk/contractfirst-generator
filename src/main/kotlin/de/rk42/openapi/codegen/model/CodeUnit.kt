package de.rk42.openapi.codegen.model

data class CodeUnit(
    val name: String,
    val operations: List<CodeOperation>
)
