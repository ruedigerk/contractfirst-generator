Contractfirst-Generator
=======================

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ruedigerk.contractfirst.generator/contractfirst-generator-parent?label=Available%20on%20Maven%20Central&style=flat-square)](https://search.maven.org/search?q=g:io.github.ruedigerk.contractfirst.generator)
[![GitHub](https://img.shields.io/github/license/ruedigerk/contractfirst-generator?label=License&style=flat-square)](https://github.com/ruedigerk/contractfirst-generator/blob/master/LICENSE.txt)

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

Here is an example for using the Maven plugin to generate server stubs:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>1.4.0</version>
       <executions>
          <execution>
             <id>generate-server</id>
             <goals>
                <goal>generate</goal>
             </goals>
             <configuration>
                <generator>server</generator>
                <inputContractFile>${project.basedir}/src/main/contract/openapi.yaml</inputContractFile>
                <outputJavaBasePackage>my.java.pkg</outputJavaBasePackage>
             </configuration>
          </execution>
       </executions>
    </plugin>

The generated server code needs the following dependencies:

    <dependency>
        <!-- Contains a JAX-RS ParamConverterProvider and a Gson MessageBodyHandler to support LocalDate and OffsetDateTime -->
        <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
        <artifactId>contractfirst-generator-server-support</artifactId>
        <version>1.4.0</version>
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
        <version>2.8.9</version>
    </dependency>


Client Generator
----------------

The client generator generates Java code that uses OkHttp, and a data model that can be serialized with Gson.

Here is an example for using the Maven plugin to generate an API client:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>1.4.0</version>
       <executions>
          <execution>
             <id>generate-client</id>
             <goals>
                <goal>generate</goal>
             </goals>
             <configuration>
                <generator>client</generator>
                <inputContractFile>${project.basedir}/src/main/contract/openapi.yaml</inputContractFile>
                <outputJavaBasePackage>my.java.pkg</outputJavaBasePackage>
             </configuration>
          </execution>
       </executions>
    </plugin>

The generated client code needs the following dependencies:

    <dependency>
        <!-- Support module for the generated client code -->
        <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
        <artifactId>contractfirst-generator-client-support</artifactId>
        <version>1.4.0</version>
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
        <version>2.8.9</version>
    </dependency>

Changelog
---------

### 1.4.1

**Fixed**
- Made the client more resilient to unusual combinations of content-types and invalid content-types.

### 1.4.0

**Added**
- Implemented basic support for multipart and form-encoded request bodies in the client generator.
- Implemented basic support for form-encoded request bodies in the server generator.

**Fixed**
- Fixed IllegalArgumentException when trying to send a request without request body for the HTTP methods POST, PUT and PATCH with the generated client.
- JSON representation of model classes is now correct when property names are not valid Java identifiers. 
- Support equally named parameters with different locations for the same operation.

**Changed**
- Changed ApiClient classes of the client generator: there are now two methods for each operation, a simplified one, and one returning an operation specific 
  result class that allows inspection of the response.
- Changed some naming in the generated client. `RequestExecutor` is now called `ApiRequestExecutor`.

### 1.3.1

**Fixed**
- Properly escape descriptions when passing them into JavaPoet as Javadoc, so that JavaPoet placeholders like $L do not crash the generator.

### 1.3.0

**Changed**
- Extensive rewrite of the client generator. The client now generates type-safe query and getter methods for working with responses, and also supports different
  styles for handling exceptions.
