package org.contractfirst.generator.java.model

import org.contractfirst.generator.model.ResponseStatusCode

data class JavaResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<JavaContent>
)