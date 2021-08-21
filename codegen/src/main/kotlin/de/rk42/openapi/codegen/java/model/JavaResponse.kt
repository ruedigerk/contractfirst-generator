package de.rk42.openapi.codegen.java.model

import de.rk42.openapi.codegen.model.ResponseStatusCode

data class JavaResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<JavaContent>
)
