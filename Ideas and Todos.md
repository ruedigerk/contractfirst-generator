Ideas and Todos
===============

## General / in the parser

- Support type "string", format "byte" (Base64 encoded binary data). 
- Add a usage description with examples to the help Mojo of the Maven plugin and to the project Readme file.
- Add support for $refs with description, e.g., in object schema properties (this is not supported by the JSON schema specification)???
- Array or object type schemas in request parameters
- Support for oneOf, allOf, anyOf in schemas
- Using Jackson instead of Gson for Serializing JSON.
- Response headers, see "headers" at https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responseObject
- Status code ranges in responses, e.g. "2XX", see https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responsesObject
- Enums of a different primitive type than string.
- Add JSON-Pointers to exceptions/error messages to pinpoint the error origin.
- Print out the YAML in case of error, as swagger-parser inlines external references and the error locations are based on this transformed contract.
  
## Code-Generation, JavaPoet

- Properly use NameAllocator with scopes, see https://github.com/square/wire/blob/d48be72904d7f6e1458b762cd936b1a7069c2813/wire-java-generator/src/main/java/com/squareup/wire/java/JavaGenerator.java#L1278-L1403
- Introduce a Kotlin DSL for JavaPoet, like the ones below or a custom one
  - https://github.com/hendraanggrian/javapoet-ktx
  - https://github.com/dump247/javapoet-dsl

## Server Generator

- Wildcard content types in operation responses (the server does not generate response methods that allow setting the content type)

## Client Generator

- Support request body with media type "application/x-www-form-urlencoded".
- Make it possible to select the desired response content type (via Accept header), if the contract defines multiple response content types.
- Optionally enable request body and parameter validation using BeanValidation.
