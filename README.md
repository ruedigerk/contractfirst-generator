OpenAPI Codegen
===============

This project is a code generator for OpenAPI 3 contracts.

Currently unsupported features
------------------------------

### Everywhere

- Enums of a different primitive type than string.
- Request body and parameter validation in the client.
- Status code ranges in responses, e.g. "2XX", see https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responsesObject
- Response headers, see "headers" at https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.2.md#responseObject

### Client-Generator

- Array or object type schemas in request parameters 

### Server-Generator