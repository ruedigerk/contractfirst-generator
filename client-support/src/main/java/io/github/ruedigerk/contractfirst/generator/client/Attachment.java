package io.github.ruedigerk.contractfirst.generator.client;

import java.io.File;
import java.io.InputStream;

/**
 * Represents a file/binary body part of a multipart request body. It contains the content, file name and media type of the body part. It is instantiated
 * using static factory methods.
 */
public class Attachment {

  private final Object content;
  private final String fileName;
  private final String mediaType;

  private Attachment(Object content, String fileName, String mediaType) {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("The fileName of an attachment cannot be null or empty.");
    }
    if (mediaType == null || mediaType.isEmpty()) {
      throw new IllegalArgumentException("The mediaType of an attachment cannot be null or empty.");
    }

    this.content = content;
    this.fileName = fileName;
    this.mediaType = mediaType;
  }

  /**
   * Creates an attachment from the specified file and the specified media type.
   */
  public static Attachment of(File file, String mediaType) {
    return new Attachment(file, file.getName(), mediaType);
  }

  /**
   * Creates an attachment from the specified file and the specified media type. Uses the specified file name instead of the name of the specified file.
   */
  public static Attachment of(File file, String fileName, String mediaType) {
    return new Attachment(file, fileName, mediaType);
  }

  /**
   * Creates an attachment by using the specified InputStream as content, the specified file name and the specified media type.
   */
  public static Attachment of(InputStream content, String fileName, String mediaType) {
    return new Attachment(content, fileName, mediaType);
  }

  /**
   * Creates an attachment by using the specified byte array as content, the specified file name and the specified media type.
   */
  public static Attachment of(byte[] content, String fileName, String mediaType) {
    return new Attachment(content, fileName, mediaType);
  }

  /**
   * Content can be either of type java.io.File, java.io.InputStream or byte[].
   */
  public Object getContent() {
    return content;
  }

  public String getFileName() {
    return fileName;
  }

  public String getMediaType() {
    return mediaType;
  }
}
