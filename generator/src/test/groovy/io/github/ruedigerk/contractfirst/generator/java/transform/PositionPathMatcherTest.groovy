package io.github.ruedigerk.contractfirst.generator.java.transform

import spock.lang.Specification

class PositionPathMatcherTest extends Specification {

  def "matches start works as expected"() {
    given:
    def matcher = PositionPathMatcher.of("components,schemas,<typeName>")

    expect:
    matcher.matchesStart(['components', 'schemas', 'Item', "items"]) == new PositionPathMatcher.Result(['components': 'components', 'schemas': 'schemas', 'typeName': 'Item'], ['items'])
  }
}
