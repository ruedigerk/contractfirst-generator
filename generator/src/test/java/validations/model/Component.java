package validations.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Test validations
 */
public class Component {
  /**
   * Test integer validations too large for Java int.
   */
  @NotNull
  @Min(-999999999L)
  @Max(-999999999L)
  private Integer intMinMax;

  /**
   * Test integer validations too large for Java int.
   */
  @Min(-9999999999999L)
  @Max(-9999999999999L)
  private Long longMinMax;

  /**
   * Test integer validations too large for Java long.
   */
  @DecimalMin("-99999999999999999999999999")
  @DecimalMax("99999999999999999999999999")
  private BigInteger bigIntMinMax;

  /**
   * Test size validation on arrays and validation on array elements.
   */
  @Size(
      min = 1,
      max = 1000
  )
  private List<@Size(min = 2, max = 4) @Pattern(regexp = "^\\d+$") String> validatedStrings = new ArrayList<>();

  /**
   * Test validation on array elements of type object.
   */
  @Valid
  private List<@Valid ComponentValidatedObjectsItem> validatedObjects = new ArrayList<>();

  /**
   * Test validation on map values.
   */
  private Map<String, @Size(min = 1, max = 10) String> validatedMap = new HashMap<>();

  /**
   * Test length and pattern validation on strings.
   */
  @Size(
      min = 1,
      max = 1000
  )
  @Pattern(
      regexp = "^\\w+$"
  )
  private String text;

  public Component intMinMax(Integer intMinMax) {
    this.intMinMax = intMinMax;
    return this;
  }

  public Integer getIntMinMax() {
    return intMinMax;
  }

  public void setIntMinMax(Integer intMinMax) {
    this.intMinMax = intMinMax;
  }

  public Component longMinMax(Long longMinMax) {
    this.longMinMax = longMinMax;
    return this;
  }

  public Long getLongMinMax() {
    return longMinMax;
  }

  public void setLongMinMax(Long longMinMax) {
    this.longMinMax = longMinMax;
  }

  public Component bigIntMinMax(BigInteger bigIntMinMax) {
    this.bigIntMinMax = bigIntMinMax;
    return this;
  }

  public BigInteger getBigIntMinMax() {
    return bigIntMinMax;
  }

  public void setBigIntMinMax(BigInteger bigIntMinMax) {
    this.bigIntMinMax = bigIntMinMax;
  }

  public Component validatedStrings(List<String> validatedStrings) {
    this.validatedStrings = validatedStrings;
    return this;
  }

  public List<String> getValidatedStrings() {
    return validatedStrings;
  }

  public void setValidatedStrings(List<String> validatedStrings) {
    this.validatedStrings = validatedStrings;
  }

  public Component validatedObjects(List<ComponentValidatedObjectsItem> validatedObjects) {
    this.validatedObjects = validatedObjects;
    return this;
  }

  public List<ComponentValidatedObjectsItem> getValidatedObjects() {
    return validatedObjects;
  }

  public void setValidatedObjects(List<ComponentValidatedObjectsItem> validatedObjects) {
    this.validatedObjects = validatedObjects;
  }

  public Component validatedMap(Map<String, String> validatedMap) {
    this.validatedMap = validatedMap;
    return this;
  }

  public Map<String, String> getValidatedMap() {
    return validatedMap;
  }

  public void setValidatedMap(Map<String, String> validatedMap) {
    this.validatedMap = validatedMap;
  }

  public Component text(String text) {
    this.text = text;
    return this;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    Component o = (Component) other;
    return Objects.equals(intMinMax, o.intMinMax)
        && Objects.equals(longMinMax, o.longMinMax)
        && Objects.equals(bigIntMinMax, o.bigIntMinMax)
        && Objects.equals(validatedStrings, o.validatedStrings)
        && Objects.equals(validatedObjects, o.validatedObjects)
        && Objects.equals(validatedMap, o.validatedMap)
        && Objects.equals(text, o.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intMinMax, longMinMax, bigIntMinMax, validatedStrings, validatedObjects, validatedMap, text);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", intMinMax=").append(intMinMax);
    builder.append(", longMinMax=").append(longMinMax);
    builder.append(", bigIntMinMax=").append(bigIntMinMax);
    builder.append(", validatedStrings=").append(validatedStrings);
    builder.append(", validatedObjects=").append(validatedObjects);
    builder.append(", validatedMap=").append(validatedMap);
    builder.append(", text=").append(text);
    return builder.replace(0, 2, "Component{").append('}').toString();
  }
}
