package client.model;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class ProblematicName {
  @SerializedName("2name")
  private String _2name;

  @SerializedName("name-and-value")
  private String nameAndValue;

  @SerializedName("problemat%c")
  private ProblematicNameProblematC problematC;

  public ProblematicName _2name(String _2name) {
    this._2name = _2name;
    return this;
  }

  public String get_2name() {
    return _2name;
  }

  public void set_2name(String _2name) {
    this._2name = _2name;
  }

  public ProblematicName nameAndValue(String nameAndValue) {
    this.nameAndValue = nameAndValue;
    return this;
  }

  public String getNameAndValue() {
    return nameAndValue;
  }

  public void setNameAndValue(String nameAndValue) {
    this.nameAndValue = nameAndValue;
  }

  public ProblematicName problematC(ProblematicNameProblematC problematC) {
    this.problematC = problematC;
    return this;
  }

  public ProblematicNameProblematC getProblematC() {
    return problematC;
  }

  public void setProblematC(ProblematicNameProblematC problematC) {
    this.problematC = problematC;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    ProblematicName o = (ProblematicName) other;
    return Objects.equals(_2name, o._2name)
        && Objects.equals(nameAndValue, o.nameAndValue)
        && Objects.equals(problematC, o.problematC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_2name, nameAndValue, problematC);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", _2name=").append(_2name);
    builder.append(", nameAndValue=").append(nameAndValue);
    builder.append(", problematC=").append(problematC);
    return builder.replace(0, 2, "ProblematicName{").append('}').toString();
  }
}
