package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.transform.Collections.takeIfAllElementsNotNull

/**
 * A Regex-inspired matcher for the path of a [io.github.ruedigerk.contractfirst.generator.openapi.Position], i.e. a list of strings.
 */
class PositionPathMatcher private constructor(
    private val segmentMatchers: List<(String) -> MatchResult?>
) {

  fun matchesStart(input: List<String>): Result? {
    val match: Map<String, String>? = input.takeIf { it.size >= segmentMatchers.size }
        ?.zip(segmentMatchers) { segment, matcher -> matcher(segment) }
        ?.takeIfAllElementsNotNull()
        ?.associate { (name, match) -> Pair(name, match) }

    return match?.let { Result(it, input.drop(segmentMatchers.size)) }
  }

  data class Result(val match: Map<String, String>, val rest: List<String>)

  private data class MatchResult(val name: String, val match: String)

  companion object {

    @JvmStatic
    fun of(pattern: String): PositionPathMatcher = PositionPathMatcher(pattern.split(',').map { segment ->
      when {
        segment.startsWith('<') && segment.endsWith('>') -> variablePathSegmentMatcher(segment.slice(1 until segment.lastIndex))
        else -> constantPathSegmentMatcher(segment)
      }
    })

    private fun constantPathSegmentMatcher(value: String): (String) -> MatchResult? = { input -> MatchResult(value, input).takeIf { input == value } }
    private fun variablePathSegmentMatcher(name: String): (String) -> MatchResult? = { input -> MatchResult(name, input) }
  }
}
