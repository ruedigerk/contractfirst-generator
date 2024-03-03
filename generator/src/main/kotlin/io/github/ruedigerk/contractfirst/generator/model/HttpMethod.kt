package io.github.ruedigerk.contractfirst.generator.model

/**
 * Represents an HTTP method.
 */
enum class HttpMethod {

  GET, 
  PUT, 
  POST, 
  DELETE, 
  OPTIONS, 
  HEAD, 
  PATCH,
  TRACE;

  companion object {

    private val METHOD_NAMES = HttpMethod.entries.map { it.name }.toSet()
    
    @JvmStatic
    operator fun invoke(method: String): HttpMethod {
      return HttpMethod.valueOf(method.uppercase())
    }
    
    fun of(method: String): HttpMethod? = when(val name = method.uppercase()) {
      in METHOD_NAMES -> HttpMethod.valueOf(name)
      else -> null
    }
  }
}