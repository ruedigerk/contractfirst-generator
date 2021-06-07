package de.rk42.openapi.codegen.model.java

data class JavaOperation(
    val javaIdentifier: String,
    val javadoc: String?,
    val path: String,
    val method: String,
    val requestBodyMediaTypes: List<String>,
    val parameters: List<JavaParameter>,
    val responses: List<JavaResponse>
)