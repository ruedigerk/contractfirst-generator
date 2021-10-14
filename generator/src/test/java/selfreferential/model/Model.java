package selfreferential.model;

import java.util.Objects;
import javax.validation.Valid;

/**
 * A self-referential Model
 */
public class Model {
  private String name;

  /**
   * A self-referential Model
   */
  @Valid
  private Model next;

  public Model name(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Model next(Model next) {
    this.next = next;
    return this;
  }

  public Model getNext() {
    return next;
  }

  public void setNext(Model next) {
    this.next = next;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Model o = (Model) other;
    return Objects.equals(name, o.name)
        && Objects.equals(next, o.next);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, next);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", name=").append(name);
    builder.append(", next=").append(next);
    return builder.replace(0, 2, "Model{").append('}').toString();
  }
}
