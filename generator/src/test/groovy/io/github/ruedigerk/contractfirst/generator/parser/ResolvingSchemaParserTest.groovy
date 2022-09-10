package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import spock.lang.Specification 

class ResolvingSchemaParserTest extends Specification {

  static final String APPLIANCE_PATH = "src/test/contract/modelOnlySchemas/appliance.yaml"
  static final String PATH_PREFIX = new File(APPLIANCE_PATH).getCanonicalFile().parent
  
  // Dependencies
  Log log = new Log(Mock(LogAdapter))

  def "parses device.json"() {
    given:
    def files = [
        new File(APPLIANCE_PATH),
        new File("src/test/contract/modelOnlySchemas/device.json"),
    ]

    when:
    def parsedSchemas = ResolvingSchemaParser.parseAndResolveAll(log, files)

    then:
    parsedSchemas.keySet().collect {
      rewriteBackslash(dropPrefix(it.position.file.path)) + it.position.path
    } ==~ [
        'appliance.yaml[]',
        'appliance.yaml[properties, name]',
        'appliance.yaml[properties, devices]',
        'device.json[]',
        'device.json[properties, name]',
        'device.json[properties, value]',
        'sibling/sibling.yaml[]',
        'sibling/sibling.yaml[properties, value]',
        'types/special-type.yaml[]',
        'types/special-type.yaml[properties, typeName]',
        'types/special-type.yaml[properties, specialities]',
        'types/special-type.yaml[properties, specialities, items]',
        'types/special-type.yaml[properties, specialities, items, properties, label]',
        'types/special-type.yaml[properties, specialities, items, properties, category]',
    ]
  }

  private static String dropPrefix(String path) {
    if (path.startsWith(PATH_PREFIX)) {
      return path.drop(PATH_PREFIX.length() + 1)
    } else {
      path
    }
  }

  private static String rewriteBackslash(String path) {
    path.replace('\\', '/')
  }
}