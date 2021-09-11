package org.contractfirst.generator.java.model

data class JavaSpecification(
    val operationGroups: List<JavaOperationGroup>,
    val modelFiles: List<JavaSourceFile>
)
