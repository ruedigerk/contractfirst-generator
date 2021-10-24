package combinations_server.model;

import java.util.Objects;

public class Book {
  private String title;

  private String isbn;

  public Book title(String title) {
    this.title = title;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Book isbn(String isbn) {
    this.isbn = isbn;
    return this;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Book o = (Book) other;
    return Objects.equals(title, o.title)
        && Objects.equals(isbn, o.isbn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, isbn);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", title=").append(title);
    builder.append(", isbn=").append(isbn);
    return builder.replace(0, 2, "Book{").append('}').toString();
  }
}
