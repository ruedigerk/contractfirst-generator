package org.contractfirst.generator.model

data class CtrRequestBody(
    val description: String?,
    val required: Boolean,
    val contents: List<CtrContent>
)