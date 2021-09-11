package org.contractfirst.generator.support.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Gson TypeAdapter for LocalDate, formatted in ISO-8601 format as required for the OpenAPI format "date".
 */
public class LocalDateGsonTypeAdapter extends TypeAdapter<LocalDate> {

  @Override
  public void write(JsonWriter writer, LocalDate value) throws IOException {
    if (value == null) {
      writer.nullValue();
    } else {
      writer.value(value.toString());
    }
  }

  @Override
  public LocalDate read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    } else {
      return parse(reader.nextString());
    }
  }

  private LocalDate parse(String input) {
    try {
      return LocalDate.parse(input);
    } catch (DateTimeParseException e) {
      throw new JsonParseException("'" + input + "' cannot be parsed as a LocalDate in ISO format", e);
    }
  }
}