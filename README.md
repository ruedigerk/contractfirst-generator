Contractfirst-Generator
=======================

Contractfirst-Generator is a code generator for OpenAPI 3 contracts, enabling a contract-first approach to developing REST APIs.

Currently, it consists of two different code generators:
- a server generator for generating Java-based JAX-RS server stubs and 
- a client generator for Java clients, using Gson and OkHttp.


Server Generator
----------------

The server generator generates Java interfaces annotated with JAX-RS annotations, and a data model that can be serialized with Gson.

The server generator is intended to run during the generate-sources phase of Maven, so that the generated interfaces can be implemented in application code
filling in the application logic.

For the generated data model to be serialized to JSON properly, it is necessary to register Gson as a JAX-RS MessageBodyReader and MessageBodyWriter. This
can be done by using the class `GsonMessageBodyHandler` from the contractfirst-generator-server-support artifact.

The generated server code needs the following dependencies:

    <dependency>
        <!-- Contains a JAX-RS ParamConverterProvider and a Gson MessageBodyHandler to support LocalDate and OffsetDateTime -->
        <groupId>org.contractfirst.generator</groupId>
        <artifactId>contractfirst-generator-server-support</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <!-- BeanValidation API for the generated data model -->
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.1.0.Final</version>
    </dependency>
    <dependency>
        <!-- Gson for serializing and deserializing the generated data model to and from JSON -->
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.7</version>
    </dependency>


Client Generator
----------------

The client generator generates Java code that uses OkHttp, and a data model that can be serialized with Gson.

The generated client code needs the following dependencies:

    <dependency>
        <!-- Support module for the generated client code -->
        <groupId>org.contractfirst.generator</groupId>
        <artifactId>contractfirst-generator-client-support</artifactId>
        <version>${project.version}</version>
    </dependency>
    <dependency>
        <!-- BeanValidation API for the generated data model -->
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.1.0.Final</version>
    </dependency>
    <dependency>
        <!-- Gson for serializing and deserializing the generated data model to and from JSON -->
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.7</version>
    </dependency>


### Feature ideas

- Make it possible to select the desired response content type (via Accept header), if the contract defines multiple response content types.
- Optionally enable request body and parameter validation using BeanValidation.


Unsupported features and ideas for improvement
----------------------------------------------

### General / in the parser

- Add JSON-Pointers to exceptions/error messages to pinpoint the error origin.
- Print out the YAML in case of error, to show to where the JSON-Pointers point, as swagger-parser inlines external references.
- Enums of a different primitive type than string
- Status code ranges in responses, e.g. "2XX", see https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responsesObject
- Response headers, see "headers" at https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responseObject
- Array or object type schemas in request parameters
- Support for oneOf, allOf, anyOf in schemas

### In the server

- Wildcard content types in operation responses (the server does not generate response methods that allow setting the content type)
