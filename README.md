OpenAPI Codegen
===============

This project is a code generator for OpenAPI 3 contracts. The current objective is
to generate JAX-RS based Java server stubs. 

To do's
-------
- Using JavaPoet for generating the Java source code: https://github.com/square/javapoet
- Proper parsing of schemas with generation of java class files
- Handling of references
- Support for operation responses
- Using imports instead of fully qualified class names
- Generating comments with descriptions, examples, etc.