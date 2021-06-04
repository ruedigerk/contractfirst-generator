package de.rk42.openapi.codegen.model.java

import de.rk42.openapi.codegen.model.contract.ResponseStatusCode

data class JavaResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<JavaResponseContent>
)
