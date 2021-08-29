package client.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class Clock {
  @NotNull
  private LocalDate time1;

  @NotNull
  private OffsetDateTime time2;

  public Clock time1(LocalDate time1) {
    this.time1 = time1;
    return this;
  }

  public LocalDate getTime1() {
    return time1;
  }

  public void setTime1(LocalDate time1) {
    this.time1 = time1;
  }

  public Clock time2(OffsetDateTime time2) {
    this.time2 = time2;
    return this;
  }

  public OffsetDateTime getTime2() {
    return time2;
  }

  public void setTime2(OffsetDateTime time2) {
    this.time2 = time2;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Clock o = (Clock) other;
    return Objects.equals(time1, o.time1)
        && Objects.equals(time2, o.time2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time1, time2);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", time1=").append(time1);
    builder.append(", time2=").append(time2);
    return builder.replace(0, 2, "Clock{").append('}').toString();
  }
}
