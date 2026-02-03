Ideas and Todos
===============

- Move all "production" sources from unit and integration tests to "test" sources.
- Add @Valid annotations to Object-typed ("complex") (dissected) request body parameters (and other places?)
- Add @Nonnull annotation to typesafe response builder method parameters.
- Support jSpecify nullability annotations in addition to jsr305.
- Research: can multiple content types for request bodies be supported (when they define differing schemas)?
  See JavaOperationTransformer.toBodyParameter and https://spec.openapis.org/oas/v3.0.3#considerations-for-file-uploads
- Research: does OpenAPI support sending/receiving request/response bodies of primitive type, especially string? Or only JSON and binary?
- Test/research multipart body as response body.
- Research text/plain request/response bodies.
- Support inner Enums (and classes?) for model.
- Implement all-in-one contract functionality without swagger-parser. 

## General / in the parser

- HTTP methods GET and HEAD do not allow a request body -> adjust parser accordingly.
- Support parameters defined with content instead of schema.
- Add a usage description with examples to the help Mojo of the Maven plugin and to the project Readme file.
- Add support for $refs with description, e.g., in object schema properties (although this is not supported by the JSON schema specification).
- Support for oneOf, allOf, anyOf in schemas.
- Response headers, see "headers" at https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responseObject
- Status code ranges in responses, e.g. "2XX", see https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responsesObject
- Enums of a different primitive type than string.
- Support type "string", format "byte" (Base64 encoded binary data).
  
## Code-Generation, JavaPoet

- Properly use NameAllocator with scopes, see https://github.com/square/wire/blob/d48be72904d7f6e1458b762cd936b1a7069c2813/wire-java-generator/src/main/java/com/squareup/wire/java/JavaGenerator.java#L1278-L1403
  - https://github.com/square/javapoet/blob/master/src/main/java/com/squareup/javapoet/NameAllocator.java
  - https://square.github.io/javapoet/javadoc/javapoet/com/squareup/javapoet/NameAllocator.html

## Server Generator

- Wildcard content types in operation responses (the server does not generate response methods that allow setting the content type)

## Client Generator

- Make it possible to select the desired response content type (via Accept header), if the contract defines multiple response content types.
- Optionally enable request body and parameter validation using BeanValidation.
