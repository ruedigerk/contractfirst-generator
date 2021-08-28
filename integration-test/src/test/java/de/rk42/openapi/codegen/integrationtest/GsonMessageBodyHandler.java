/*
 * Copyright (C) 2020 Sopra Financial Technology GmbH
 * Frankenstraße 146, 90461 Nürnberg, Germany
 *
 * This software is the confidential and proprietary information of
 * Sopra Financial Technology GmbH ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * Sopra Financial Technology GmbH.
 */
package de.rk42.openapi.codegen.integrationtest;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * JAX-RS MessageBodyWriter and -Reader for serializing and deserializing JSON messages with Gson.
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

  private final Gson gson = new Gson();

  @Override
  public Object readFrom(
      Class<Object> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream httpBody
  ) throws IOException, WebApplicationException {
    return parseEntity(type, httpBody);
  }

  private Object parseEntity(Class<Object> type, InputStream httpBody) throws IOException, BadRequestException {
    try (InputStreamReader reader = new InputStreamReader(httpBody, StandardCharsets.UTF_8)) {
      return gson.fromJson(reader, type);
    } catch (JsonIOException e) {
      throw new IOException(e);
    } catch (JsonSyntaxException e) {
      throw new BadRequestException("Cannot parse request entity: " + e.getMessage(), e);
    }
  }

  @Override
  public void writeTo(
      Object object,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream httpBody
  ) throws IOException, WebApplicationException {

    try (OutputStreamWriter writer = new OutputStreamWriter(httpBody, StandardCharsets.UTF_8)) {
      gson.toJson(object, type, writer);
    } catch (JsonIOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public long getSize(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }
}
