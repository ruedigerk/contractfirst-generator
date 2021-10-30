package io.github.ruedigerk.contractfirst.generator.client.internal;

import java.util.List;

/**
 * Represents the definition of a request body that is either a multipart body, or an application/x-www-form-urlencoded body.
 */
public class MultipartRequestBody {

  private final List<BodyPart> bodyParts;

  public MultipartRequestBody(List<BodyPart> bodyParts) {
    this.bodyParts = bodyParts;
  }

  public List<BodyPart> getBodyParts() {
    return bodyParts;
  }
}
