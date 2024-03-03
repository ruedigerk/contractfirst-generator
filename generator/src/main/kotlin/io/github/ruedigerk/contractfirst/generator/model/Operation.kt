package io.github.ruedigerk.contractfirst.generator.model

/**
 * Represents an operation in the contract.
 */
data class Operation(
    val path: String,

    /** The HTTP method of the operation in upper case (ensured by the parser). */
    val method: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val operationId: String?,
    val requestBody: RequestBody?,
    val parameters: List<Parameter>,
    val responses: List<Response>,

    /** The position of the operation in the contract */
    val position: Position
) {

  val pathAndMethod: PathAndMethod
    get() = PathAndMethod(path, method)
  
  /**
   * The combination of a path and an HTTP method uniquely identifies an operation in a contract.
   */
  data class PathAndMethod private constructor(val path: String, val method: String) {

    companion object {
      
      operator fun invoke(path: String, method: String): PathAndMethod = PathAndMethod(path, method.uppercase())
    }
  }
}

