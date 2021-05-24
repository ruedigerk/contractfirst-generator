package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrPathItem
import de.rk42.openapi.codegen.model.contract.CtrSchema
import de.rk42.openapi.codegen.model.contract.CtrSchemaRef
import de.rk42.openapi.codegen.model.contract.CtrSpecification
import de.rk42.openapi.codegen.model.intermediate.ItmOperation
import de.rk42.openapi.codegen.model.intermediate.ItmSpecification

/**
 * TODO: Is this still needed (includes the whole intermediate model)?
 */
class ContractToIntermediateModelTransformer {

  fun transform(specification: CtrSpecification): ItmSpecification {
    return ItmSpecification(toItmOperations(specification.pathItems))
  }

  private fun toItmOperations(pathItems: List<CtrPathItem>): List<ItmOperation> {
    return pathItems.flatMap { pathItem -> pathItem.operations.map { operation -> toItmOperation(pathItem, operation) } }
  }

  private fun toItmOperation(pathItem: CtrPathItem, operation: CtrOperation): ItmOperation {
    return ItmOperation(
        pathItem.path,
        operation.method,
        operation.tags,
        operation.summary,
        operation.description,
        operation.operationId,
        operation.parameters
    )
  }
  
  private class SchemaResolver(schemas: MutableMap<CtrSchemaRef, CtrSchema>) {
    
    private val unresolvedSchemas: MutableMap<CtrSchemaRef, CtrSchema> = schemas.toMutableMap()
    private val resolvedSchemas: MutableMap<CtrSchemaRef, CtrSchema> = mutableMapOf()
    
    fun resolve(ref: CtrSchemaRef): CtrSchema {
      if (resolvedSchemas.containsKey(ref)) {
        return resolvedSchemas[ref]!!
      }
      
      val resolvedSchema = resolve(unresolvedSchemas[ref]!!)
      resolvedSchemas[ref] = resolvedSchema
      unresolvedSchemas.remove(ref)
      
      return resolvedSchema
    }

    private fun resolve(schema: CtrSchema): CtrSchema {
      TODO("Implement recursive resolving of refs inside schema")
    }
  }
}