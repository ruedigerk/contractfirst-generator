package uspto.model;

import java.math.BigInteger;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class PerformSearchRequestBody {
  /**
   * Uses Lucene Query Syntax in the format of propertyName:value, propertyName:[num1 TO num2] and date range format: propertyName:[yyyyMMdd TO yyyyMMdd]. In the response please see the 'docs' element which has the list of record objects. Each record structure would consist of all the fields and their corresponding values.
   */
  @NotNull
  private String criteria;

  /**
   * Starting record number. Default value is 0.
   */
  private BigInteger start;

  /**
   * Specify number of rows to be returned. If you run the search with default values, in the response you will see 'numFound' attribute which will tell the number of records available in the dataset.
   */
  private BigInteger rows;

  public PerformSearchRequestBody criteria(String criteria) {
    this.criteria = criteria;
    return this;
  }

  public String getCriteria() {
    return criteria;
  }

  public void setCriteria(String criteria) {
    this.criteria = criteria;
  }

  public PerformSearchRequestBody start(BigInteger start) {
    this.start = start;
    return this;
  }

  public BigInteger getStart() {
    return start;
  }

  public void setStart(BigInteger start) {
    this.start = start;
  }

  public PerformSearchRequestBody rows(BigInteger rows) {
    this.rows = rows;
    return this;
  }

  public BigInteger getRows() {
    return rows;
  }

  public void setRows(BigInteger rows) {
    this.rows = rows;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    PerformSearchRequestBody o = (PerformSearchRequestBody) other;
    return Objects.equals(criteria, o.criteria)
        && Objects.equals(start, o.start)
        && Objects.equals(rows, o.rows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(criteria, start, rows);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", criteria=").append(criteria);
    builder.append(", start=").append(start);
    builder.append(", rows=").append(rows);
    return builder.replace(0, 2, "PerformSearchRequestBody{").append('}').toString();
  }
}
