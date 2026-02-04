Contractfirst-Generator
=======================

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ruedigerk.contractfirst.generator/contractfirst-generator-parent?label=Available%20on%20Maven%20Central&style=flat-square)](https://search.maven.org/search?q=g:io.github.ruedigerk.contractfirst.generator)
[![GitHub](https://img.shields.io/github/license/ruedigerk/contractfirst-generator?label=License&style=flat-square)](https://github.com/ruedigerk/contractfirst-generator/blob/master/LICENSE.txt)

Contractfirst-Generator is a code generator for OpenAPI 3 contracts, enabling a contract-first approach to developing REST APIs.

Currently, it consists of the following generators:
- Server
  - Jakarta Restful Web Services (JAX-RS): Generates interfaces to implement as JAX-RS resource classes, and model files (Gson or Jackson).
  - Spring Web MVC: Generates interfaces to implement as Spring REST controllers, and model files (Gson or Jackson).
- Client
  - OkHttp/Gson: Generates a Java client based on OkHttp and Gson (Jackson is not supported).
- Model-only
  - With only schema files as input, generates the corresponding Java model files  (Gson or Jackson).


Server Generator
----------------

The server generator generates annotated Java interfaces, and a data model that can be serialized as JSON.

The server generator is intended to run during the generate-sources phase of Maven, so that the generated interfaces can be implemented in application code
filling in the application logic.

Configuration:
- Use the configuration option `generatorVariant` to select the framework, either `jax-rs` (default) or `spring-web`.
- Use the configuration option `modelVariant` to select the JSON serializer, either `gson` or `jackson`.
- For other configuration options, refer to the help output of the Maven plugin, using
  `mvn io.github.ruedigerk.contractfirst.generator:contractfirst-generator-maven-plugin:help -Dgoal=generate -Ddetail`

### JAX-RS

For the generated data model to be serialized to JSON properly when using JAX-RS, it is necessary to register Gson as a JAX-RS MessageBodyReader and 
MessageBodyWriter. This can be done by using the class `GsonMessageBodyHandler` from the `contractfirst-generator-server-support` artifact.

Here is an example for using the Maven plugin to generate JAX-RS interfaces with a Gson model:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>2.0.0</version>
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
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <!-- Jakarta Validation API for the generated data model -->
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>
    <dependency>
        <!-- Gson for serializing and deserializing the generated data model to and from JSON -->
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.13.2</version>
    </dependency>

Additionally, if using the option `outputJavaModelUseJsr305NullabilityAnnotations` there needs to be a dependency for these annotations, like:

    <dependency>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <version>3.0.2</version>
    </dependency>

### Spring Web MVC

Here is an example for using the Maven plugin to generate Spring REST controller interfaces with a Jackson model:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>2.0.0</version>
       <executions>
          <execution>
             <id>generate-server</id>
             <goals>
                <goal>generate</goal>
             </goals>
             <configuration>
                <generator>server</generator>
                <generatorVariant>spring-web</generatorVariant>
                <modelVariant>jackson</modelVariant>
                <inputContractFile>${project.basedir}/src/main/contract/openapi.yaml</inputContractFile>
                <outputJavaBasePackage>my.java.pkg</outputJavaBasePackage>
             </configuration>
          </execution>
       </executions>
    </plugin>

The generated server code needs Spring dependencies for Web MVC and Jakarta Validation, e.g.:

    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-webmvc</artifactId>
       <scope>test</scope>
    </dependency>
    <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
       <scope>test</scope>
    </dependency>

Additionally, if using the option `outputJavaModelUseJsr305NullabilityAnnotations` there needs to be a dependency for these annotations, like:

    <dependency>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <version>3.0.2</version>
    </dependency>


Client Generator
----------------

The client generator generates Java code that uses OkHttp, and a data model that is serialized using Gson. No other variants are currently supported for the
client generator.

Configuration:
- Use the configuration option `generator` with a value of `client`.
- For other configuration options, refer to the help output of the Maven plugin, using
  `mvn io.github.ruedigerk.contractfirst.generator:contractfirst-generator-maven-plugin:help -Dgoal=generate -Ddetail`

Here is an example for using the Maven plugin to generate an API client:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>2.0.0</version>
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
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <!-- Jakarta Validation API for the generated data model -->
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>
    <dependency>
        <!-- Gson for serializing and deserializing the generated data model to and from JSON -->
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.13.2</version>
    </dependency>

Additionally, if using the option `outputJavaModelUseJsr305NullabilityAnnotations` there needs to be a dependency for these annotations, like:

    <dependency>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <version>3.0.2</version>
    </dependency>


Model-only Generator
--------------------

The model-only generator generates a data model for serialization with either Gson or Jackson.

Configuration:
- Use the configuration option `generator` with a value of `model-only`.
- Use the configuration option `modelVariant` to select the JSON serializer, either `gson` or `jackson`.
- For other configuration options, refer to the help output of the Maven plugin, using
  `mvn io.github.ruedigerk.contractfirst.generator:contractfirst-generator-maven-plugin:help -Dgoal=generate -Ddetail`

### Gson

For the generated data model to be serialized to JSON properly, it is necessary to register Gson as a JAX-RS MessageBodyReader and MessageBodyWriter. This
can be done by using the class `GsonMessageBodyHandler` from the contractfirst-generator-server-support artifact.

Here is an example for using the Maven plugin to generate only model files:

    <plugin>
       <artifactId>contractfirst-generator-maven-plugin</artifactId>
       <groupId>io.github.ruedigerk.contractfirst.generator</groupId>
       <version>2.0.0</version>
       <executions>
          <execution>
             <id>generate-model-only</id>
             <goals>
                <goal>generate</goal>
             </goals>
             <configuration>
                <generator>model-only</generator>
                <!-- In model-only mode, inputContractFile can point to a directory which is then searched recusively for schema files ending in .json or .yaml -->
                <inputContractFile>${project.basedir}/src/main/schema</inputContractFile>
                <outputJavaBasePackage>my.java.pkg</outputJavaBasePackage>
             </configuration>
          </execution>
       </executions>
    </plugin>

The generated Gson model needs the following dependencies:

    <dependency>
        <!-- Jakarta Validation API for the generated data model -->
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>
    <dependency>
        <!-- Gson for serializing and deserializing the generated data model to and from JSON -->
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.13.2</version>
    </dependency>

Additionally, if using the option `outputJavaModelUseJsr305NullabilityAnnotations` there needs to be a dependency for these annotations, like:

    <dependency>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <version>3.0.2</version>
    </dependency>


Description for Maven Plugin Goal 'generate'
--------------------------------------------

Goal for generating sources from an OpenAPI contract.

Available parameters:

    addAsSourceRoot (Default: true)
      Whether to add the generated sources directory to the Maven source roots;
      defaults to true.
      User property: openapi.generator.maven.plugin.add-as-source-root

    addAsTestSource (Default: false)
      Whether to add the generated sources directory as a test source directory
      instead of a main compile source directory; defaults to false.
      User property: openapi.generator.maven.plugin.add-as-test-source

    generator
      The type of generator to use for code generation; allowed values are:
      "server", "client", "model-only".
      Required: Yes
      User property: openapi.generator.maven.plugin.generator

    generatorVariant
      The variant of the generator to use for code generation; allowed values
      depend on the selected generator: - server generator: "jax-rs" (default),
      "spring-web" - client generator: "okhttp" (default) - model-only
      generator: "model-only" (default)
      User property: openapi.generator.maven.plugin.generatorVariant

    inputContractFile
      The path to the file containing the OpenAPI contract to use as input; in
      case of the model-only generator, this should point to a single
      JSON-Schema file in YAML or JSON format, or to a directory which is
      recursively searched for JSON-Schema files.
      Required: Yes
      User property: openapi.generator.maven.plugin.inputContractFile

    modelVariant (Default: gson)
      The variant of the model to use for code generation; allowed values are:
      "gson", "jackson".
      User property: openapi.generator.maven.plugin.modelVariant

    outputContract (Default: false)
      Whether to output the parsed contract as an all-in-one contract.
      User property: openapi.generator.maven.plugin.outputContract

    outputContractFile (Default: openapi.yaml)
      The file name of the all-in-one contract file to output; only used when
      outputContract is true.
      User property: openapi.generator.maven.plugin.outputContractFile

    outputDir (Default: ${project.build.directory}/generated-sources/contractfirst-generator)
      The target directory for writing the generated sources to.
      User property: openapi.generator.maven.plugin.outputDir

    outputJavaBasePackage
      The Java package to put generated classes into.
      Required: Yes
      User property: openapi.generator.maven.plugin.outputJavaBasePackage

    outputJavaModelNamePrefix
      The prefix for Java model class names; defaults to the empty String.
      User property: openapi.generator.maven.plugin.outputJavaModelNamePrefix

    outputJavaModelUseJsr305NullabilityAnnotations (Default: false)
      Whether to generate JSR-305 nullability annotations for the getter and
      setter methods of the model classes.
      User property: openapi.generator.maven.plugin.outputJavaModelUseJsr305NullabilityAnnotations

    outputJavaPackageMirrorsSchemaDirectory (Default: false)
      Whether the Java packages of the generated model files are mirroring the
      directory structure of the schema files.
      User property: openapi.generator.maven.plugin.outputJavaPackageMirrorsSchemaDirectory

    outputJavaPackageSchemaDirectoryPrefix
      The path prefix to cut from the schema file directories when determining
      Java packages for model files; defaults to the directory of the
      inputContractFile; this is only used, when
      outputJavaPackageMirrorsSchemaDirectory is true.
      User property: openapi.generator.maven.plugin.outputJavaPackageSchemaDirectoryPrefix

    skip (Default: false)
      Skip execution of this plugin; defaults to false.
      User property: openapi.generator.maven.plugin.skip


Changelog
---------

### 2.0.0

**Added**
- Added support for generating Spring Web server stubs. 
- Added support for generating Jackson-based models. 
- Added the following configurations options
  - generatorVariant
  - modelVariant
  - addAsSourceRoot

**Changed**
- The generated model files now contain annotations requiring Jakarta Validation in version 3.0.0 or above.
- The JAX-RS generator now targets Jakarta 10, i.e., Jakarta RESTful Web Services 3.1.

### 1.9.0

**Added**
- Support optional body parts for multipart bodies.
- Support Collection-typed values for request bodies.
- Ignore enum definitions with a different type than string.

### 1.8.0

**Added**
- Added a new configuration option `addAsTestSource` for the Maven plugin, which allows adding the directory with the generated sources as a test source 
  root instead of a regular source root.

**Changed**
- The method signatures generated for multipart request bodies for the client have changed.
- Updated language dependencies.

**Fixed**
- The generated client is now able to properly handle multipart request bodies, and to properly upload files in multipart requests.

### 1.7.0

**Changed**
- Enabled serialization of array-valued parameters on the client. See [OpenAPI 3.0.3 Parameter Object](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.0.md#parameter-object)
  - Query parameters are serialized in "form" style. 
  - Header parameters are serialized in "form" style (this is in violation with the OpenAPI specification but works with JAX-RS out of the box). 
  - Path parameters are serialized in "simple" style (comma-separated lists).
- Updated dependencies.

### 1.6.1

**Changed**
- Updated various dependencies.

### 1.6.0

**Added**
- Added `model-only` generator type for generating a Java model for a set of JSON-Schema files.
- New configuration option `outputJavaModelUseJsr305NullabilityAnnotations` for adding JSR-305 nullability annotations to the generated model's getter and
  setter methods.
- New configuration option `outputJavaPackageMirrorsSchemaDirectory` for mirroring the input schema file directory structure in the package structure of 
  generated Java model files.
- New configuration option `outputJavaPackageSchemaDirectoryPrefix` for setting the prefix "directory" which is stripped from all input schema files when 
  mirroring the schema directory in the Java model files. Only used if `outputJavaPackageMirrorsSchemaDirectory` is true.

### 1.5.2

**Fixed**
- Fixed a bug that made the client return no content when the server sent a response not defined in the contract, instead of throwing 
  ApiClientIncompatibleResponseException. This only happened when there was no "default" status code defined in the contract.

### 1.5.1

**Fixed**
- Fixed a bug that lead to uncompilable code if multiple operations used the same generic response type.

### 1.5.0

**Changed**
- The generated model files now contain annotations requiring BeanValidation in version 2.0.0.Final or above.

**Fixed**
- Fixed a bug in generated `@Min` and `@Max` annotations when the values were larger than representable by `int`.

**Added**
- Added the capability that fields of type List or Map in the generated model classes can have validation annotations on the element type, i.e. 
  `private List<@Size(min = 2, max = 4) String> theList;`.

### 1.4.1

**Fixed**
- Made the client more resilient to unusual combinations of content-types and invalid content-types.
- Implemented support for enums in x-www-form-urlencoded request bodies, where the Java names of the constants do not match the original names.

**Added**
- Added fallback to the client generator, to try to deserialize response entities sent with a JSON content type, even if the contract does not define this. 
  This quirk is added, because there seem to be a lot of contracts in the wild that erroneously declare some none-JSON content type in the contract but actually
  send JSON encoded response entities.
 
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
