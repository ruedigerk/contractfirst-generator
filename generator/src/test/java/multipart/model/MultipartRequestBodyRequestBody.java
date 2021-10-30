package multipart.model;

import java.util.Objects;

public class MultipartRequestBodyRequestBody {
  private String partOne;

  private String partTwo;

  public MultipartRequestBodyRequestBody partOne(String partOne) {
    this.partOne = partOne;
    return this;
  }

  public String getPartOne() {
    return partOne;
  }

  public void setPartOne(String partOne) {
    this.partOne = partOne;
  }

  public MultipartRequestBodyRequestBody partTwo(String partTwo) {
    this.partTwo = partTwo;
    return this;
  }

  public String getPartTwo() {
    return partTwo;
  }

  public void setPartTwo(String partTwo) {
    this.partTwo = partTwo;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    MultipartRequestBodyRequestBody o = (MultipartRequestBodyRequestBody) other;
    return Objects.equals(partOne, o.partOne)
        && Objects.equals(partTwo, o.partTwo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(partOne, partTwo);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", partOne=").append(partOne);
    builder.append(", partTwo=").append(partTwo);
    return builder.replace(0, 2, "MultipartRequestBodyRequestBody{").append('}').toString();
  }
}
