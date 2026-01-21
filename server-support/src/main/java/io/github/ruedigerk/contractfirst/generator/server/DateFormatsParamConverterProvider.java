
package io.github.ruedigerk.contractfirst.generator.server;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * JAX-RS ParamConverterProvider to enable parameters in the date and date-time formats to be serialized to LocalDate and OffsetDateTime instances.
 */
@Provider
public class DateFormatsParamConverterProvider implements ParamConverterProvider {

  private final ParamConverter<LocalDate> localDateConverter;
  private final ParamConverter<OffsetDateTime> offsetDateTimeConverter;

  public DateFormatsParamConverterProvider() {
    localDateConverter = new LocalDateConverter();
    offsetDateTimeConverter = new OffsetDateTimeConverter();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
    if (rawType.equals(LocalDate.class)) {
      return (ParamConverter<T>) localDateConverter;
    } else if (rawType.equals(OffsetDateTime.class)) {
      return (ParamConverter<T>) offsetDateTimeConverter;
    } else {
      return null;
    }
  }

  private static class LocalDateConverter implements ParamConverter<LocalDate> {

    @Override
    public LocalDate fromString(String value) {
      return LocalDate.parse(value);
    }

    @Override
    public String toString(LocalDate value) {
      return value.toString();
    }
  }

  private static class OffsetDateTimeConverter implements ParamConverter<OffsetDateTime> {

    @Override
    public OffsetDateTime fromString(String value) {
      return OffsetDateTime.parse(value);
    }

    @Override
    public String toString(OffsetDateTime value) {
      return value.toString();
    }
  }
}