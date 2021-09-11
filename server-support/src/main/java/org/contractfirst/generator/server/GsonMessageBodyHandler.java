package org.contractfirst.generator.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.contractfirst.generator.support.gson.LocalDateGsonTypeAdapter;
import org.contractfirst.generator.support.gson.OffsetDateTimeGsonTypeAdapter;

/**
 * JAX-RS MessageBodyWriter and -Reader for serializing and deserializing JSON messages with Gson.
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(LocalDate.class, new LocalDateGsonTypeAdapter())
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeGsonTypeAdapter())
      .create();

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
