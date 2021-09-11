package server.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class Manual {
  @NotNull
  private String title;

  @NotNull
  private String content;

  public Manual title(String title) {
    this.title = title;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Manual content(String content) {
    this.content = content;
    return this;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Manual o = (Manual) other;
    return Objects.equals(title, o.title)
        && Objects.equals(content, o.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, content);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", title=").append(title);
    builder.append(", content=").append(content);
    return builder.replace(0, 2, "Manual{").append('}').toString();
  }
}
