package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import io.github.ruedigerk.contractfirst.generator.model.HttpMethod
import io.github.ruedigerk.contractfirst.generator.model.Operation
import io.github.ruedigerk.contractfirst.generator.model.Position
import spock.lang.Specification
import spock.lang.Subject

class JavaTypeNameGeneratorTest extends Specification {

  static final String MODEL_PACKAGE = "package.api"

  Map<Operation.PathAndMethod, String> operationMethodNames = [
      new Operation.PathAndMethod("/path/op", HttpMethod.GET): "usesOperationId"
  ]
  
  JavaConfiguration configuration = new JavaConfiguration(
      "ignored",
      "ignored",
      MODEL_PACKAGE,
      "ignored",
      true,
      "/stripped",
      "Prefix",
      false
  )

  @Subject
  JavaTypeNameGenerator generator = new JavaTypeNameGenerator(new Log(Mock(LogAdapter)), configuration, operationMethodNames)

  def "works as expected"() {
    expect:
    generator.determineName(position) == expected

    where:
    position                                                                                  | expected
    pos("/dir/contract.yaml", ["components", "schemas", "Model"])                             | name(MODEL_PACKAGE, "PrefixModel")
    pos("/dir/contract.yaml", ["components", "schemas", "Model", "items"])                    | name(MODEL_PACKAGE, "PrefixModelItem")
    pos("/dir/contract.yaml", ["components", "schemas", "Model", "properties", "child"])      | name(MODEL_PACKAGE, "PrefixModelChild")
    pos("/dir/contract.yaml", ["paths", "/path/op", "parameters", "paramA", "schema"])        | name(MODEL_PACKAGE, "PrefixPathOpParameterParamA")
    pos("/dir/contract.yaml", ["paths", "/path/op", "GET", "parameters", "paramA", "schema"]) | name(MODEL_PACKAGE, "PrefixUsesOperationIdParameterParamA")
    pos("/dir/contract.yaml", ["paths", "/path/op", "GET", "requestBody", "content", "application/json", "schema"]) | name(MODEL_PACKAGE, "PrefixUsesOperationIdRequestBodyApplicationJson")
    pos("/dir/contract.yaml", ["paths", "/path/op", "GET", "responses", "200", "content", "application/json", "schema"]) | name(MODEL_PACKAGE, "PrefixUsesOperationIdResponse200ApplicationJson")
    pos("/stripped/schema.yaml", [])                                                          | name(MODEL_PACKAGE, "PrefixSchema")
    pos("/stripped/sub/schema.yaml", [])                                                      | name(MODEL_PACKAGE + ".sub", "PrefixSchema")
    pos("/stripped/sub/more/schema.yaml", [])                                                 | name(MODEL_PACKAGE + ".sub.more", "PrefixSchema")
    pos("/stripped/schema.yaml", ['items', 'properties', 'prop'])                             | name(MODEL_PACKAGE, "PrefixSchemaItemProp")
    pos("/stripped/sub/schema.yaml", ['items', 'properties', 'prop'])                         | name(MODEL_PACKAGE + ".sub", "PrefixSchemaItemProp")
  }

  private JavaTypeName name(String pckg, String simpleName) {
    new JavaTypeName(pckg, simpleName)
  }

  private Position pos(String file, List<String> path) {
    return new Position(new File(file), path)
  }
}
