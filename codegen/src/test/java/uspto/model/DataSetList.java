package uspto.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;

public class DataSetList {
  private BigInteger total;

  @Valid
  private List<DataSetListApis> apis = new ArrayList<>();

  public DataSetList total(BigInteger total) {
    this.total = total;
    return this;
  }

  public BigInteger getTotal() {
    return total;
  }

  public void setTotal(BigInteger total) {
    this.total = total;
  }

  public DataSetList apis(List<DataSetListApis> apis) {
    this.apis = apis;
    return this;
  }

  public List<DataSetListApis> getApis() {
    return apis;
  }

  public void setApis(List<DataSetListApis> apis) {
    this.apis = apis;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DataSetList o = (DataSetList) other;
    return Objects.equals(total, o.total)
        && Objects.equals(apis, o.apis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, apis);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", total=").append(total);
    builder.append(", apis=").append(apis);
    return builder.replace(0, 2, "DataSetList{").append('}').toString();
  }
}
