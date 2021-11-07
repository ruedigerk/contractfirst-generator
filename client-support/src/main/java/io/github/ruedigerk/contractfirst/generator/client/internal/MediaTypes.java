package io.github.ruedigerk.contractfirst.generator.client.internal;

import okhttp3.MediaType;

/**
 * Helper methods for working with media types.
 */
public class MediaTypes {

  private MediaTypes() {
    // Only static methods
  }

  public static MediaType parseNullable(String mediaType) {
    return mediaType == null ? null : MediaType.parse(mediaType);
  }

  public static boolean isJsonMediaType(String mediaType) {
    return isJsonMediaType(parseNullable(mediaType));
  }

  public static boolean isJsonMediaType(MediaType mediaType) {
    if (mediaType == null) {
      return false;
    }

    String subtype = mediaType.subtype();
    return mediaType.type().equals("application") && (subtype.equals("json") || subtype.startsWith("vnd.") && subtype.endsWith("+json"));
  }
}
