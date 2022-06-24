package server_jsr305.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import javax.annotation.Nullable;

public class ClockResponse {
  private LocalDate time1;

  private OffsetDateTime time2;

  private LocalDate pathTime;

  private LocalDate queryTime1;

  private OffsetDateTime queryTime2;

  private LocalDate headerTime1;

  private OffsetDateTime headerTime2;

  public ClockResponse time1(@Nullable LocalDate time1) {
    this.time1 = time1;
    return this;
  }

  @Nullable
  public LocalDate getTime1() {
    return time1;
  }

  public void setTime1(@Nullable LocalDate time1) {
    this.time1 = time1;
  }

  public ClockResponse time2(@Nullable OffsetDateTime time2) {
    this.time2 = time2;
    return this;
  }

  @Nullable
  public OffsetDateTime getTime2() {
    return time2;
  }

  public void setTime2(@Nullable OffsetDateTime time2) {
    this.time2 = time2;
  }

  public ClockResponse pathTime(@Nullable LocalDate pathTime) {
    this.pathTime = pathTime;
    return this;
  }

  @Nullable
  public LocalDate getPathTime() {
    return pathTime;
  }

  public void setPathTime(@Nullable LocalDate pathTime) {
    this.pathTime = pathTime;
  }

  public ClockResponse queryTime1(@Nullable LocalDate queryTime1) {
    this.queryTime1 = queryTime1;
    return this;
  }

  @Nullable
  public LocalDate getQueryTime1() {
    return queryTime1;
  }

  public void setQueryTime1(@Nullable LocalDate queryTime1) {
    this.queryTime1 = queryTime1;
  }

  public ClockResponse queryTime2(@Nullable OffsetDateTime queryTime2) {
    this.queryTime2 = queryTime2;
    return this;
  }

  @Nullable
  public OffsetDateTime getQueryTime2() {
    return queryTime2;
  }

  public void setQueryTime2(@Nullable OffsetDateTime queryTime2) {
    this.queryTime2 = queryTime2;
  }

  public ClockResponse headerTime1(@Nullable LocalDate headerTime1) {
    this.headerTime1 = headerTime1;
    return this;
  }

  @Nullable
  public LocalDate getHeaderTime1() {
    return headerTime1;
  }

  public void setHeaderTime1(@Nullable LocalDate headerTime1) {
    this.headerTime1 = headerTime1;
  }

  public ClockResponse headerTime2(@Nullable OffsetDateTime headerTime2) {
    this.headerTime2 = headerTime2;
    return this;
  }

  @Nullable
  public OffsetDateTime getHeaderTime2() {
    return headerTime2;
  }

  public void setHeaderTime2(@Nullable OffsetDateTime headerTime2) {
    this.headerTime2 = headerTime2;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ClockResponse o = (ClockResponse) other;
    return Objects.equals(time1, o.time1)
        && Objects.equals(time2, o.time2)
        && Objects.equals(pathTime, o.pathTime)
        && Objects.equals(queryTime1, o.queryTime1)
        && Objects.equals(queryTime2, o.queryTime2)
        && Objects.equals(headerTime1, o.headerTime1)
        && Objects.equals(headerTime2, o.headerTime2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time1, time2, pathTime, queryTime1, queryTime2, headerTime1, headerTime2);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", time1=").append(time1);
    builder.append(", time2=").append(time2);
    builder.append(", pathTime=").append(pathTime);
    builder.append(", queryTime1=").append(queryTime1);
    builder.append(", queryTime2=").append(queryTime2);
    builder.append(", headerTime1=").append(headerTime1);
    builder.append(", headerTime2=").append(headerTime2);
    return builder.replace(0, 2, "ClockResponse{").append('}').toString();
  }
}
