package de.rk42.openapi.codegen.model.java

data class JavaOperation(
    val javaIdentifier: String,
    val path: String,
    val method: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val requestBodyMediaTypes: List<String>,
    val parameters: List<JavaParameter>,
    val responses: List<JavaResponse>
)