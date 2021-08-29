OpenAPI Codegen
===============

This project is a code generator for OpenAPI 3 contracts.

There are two different generators: a generator for server stubs and a generator for clients.


Server Generator
----------------

The server generator generates Java interfaces annotated with JAX-RS annotations, and a data model that can be serialized with Gson.

The server generator is intended to run during the generate-sources phase of Maven, so that the generated interfaces can be implemented in application code
filling in the application logic.

The generated server code needs the following dependencies:

    <dependency>
        <!-- Contains a JAX-RS ParamConverterProvider and a Gson MessageBodyHandler to support LocalDate and OffsetDateTime -->
        <groupId>de.rk42.openapi-codegen</groupId>
        <artifactId>openapi-codegen-server-support</artifactId>
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
        <groupId>de.rk42.openapi-codegen</groupId>
        <artifactId>openapi-codegen-client-support</artifactId>
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


Unsupported OpenAPI features
----------------------------

### Everywhere

- Enums of a different primitive type than string
- Status code ranges in responses, e.g. "2XX", see https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responsesObject
- Response headers, see "headers" at https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responseObject
- Array or object type schemas in request parameters
- Support for oneOf, allOf, anyOf in schemas

# In the server

- Wildcards content types in operation responses (the server does not generate response methods that allow setting the content type)
