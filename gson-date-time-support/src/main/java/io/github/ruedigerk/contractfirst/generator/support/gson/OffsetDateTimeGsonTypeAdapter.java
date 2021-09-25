package io.github.ruedigerk.contractfirst.generator.support.gson;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Gson TypeAdapter for OffsetDateTime, formatted in ISO-8601 format as required for the OpenAPI format "date-time".
 */
public class OffsetDateTimeGsonTypeAdapter extends TypeAdapter<OffsetDateTime> {

  @Override
  public void write(JsonWriter writer, OffsetDateTime value) throws IOException {
    if (value == null) {
      writer.nullValue();
    } else {
      writer.value(value.toString());
    }
  }

  @Override
  public OffsetDateTime read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    } else {
      return parse(reader.nextString());
    }
  }

  private OffsetDateTime parse(String input) {
    try {
      return OffsetDateTime.parse(input);
    } catch (DateTimeParseException e) {
      throw new JsonParseException("'" + input + "' cannot be parsed as an OffsetDateTime in ISO format", e);
    }
  }
}
