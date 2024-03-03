package io.github.ruedigerk.contractfirst.generator.client;

import java.io.File;
import java.io.InputStream;

/**
 * Represents a file as part of a multipart request body. It is constructed using static factory methods. 
 */
public class FileBodyPart {

  private final Object content;
  private final String fileName;
  private final String mediaType;

  private FileBodyPart(Object content, String fileName, String mediaType) {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("The fileName of a FileBodyPart cannot be null or empty.");
    }
    if (mediaType == null || mediaType.isEmpty()) {
      throw new IllegalArgumentException("The mediaType of a FileBodyPart cannot be null or empty.");
    }

    this.content = content;
    this.fileName = fileName;
    this.mediaType = mediaType;
  }

  public static FileBodyPart of(File file, String mediaType) {
    return new FileBodyPart(file, file.getName(), mediaType);
  }

  public static FileBodyPart of(InputStream inputStream, String fileName, String mediaType) {
    return new FileBodyPart(inputStream, fileName, mediaType);
  }

  /**
   * Content can be either of type java.io.File or java.io.InputStream.
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
