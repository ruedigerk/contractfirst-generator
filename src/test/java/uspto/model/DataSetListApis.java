package uspto.model;

import java.util.Objects;

public class DataSetListApis {
  /**
   * To be used as a dataset parameter value
   */
  private String apiKey;

  /**
   * To be used as a version parameter value
   */
  private String apiVersionNumber;

  /**
   * The URL describing the dataset's fields
   */
  private String apiUrl;

  /**
   * A URL to the API console for each API
   */
  private String apiDocumentationUrl;

  public DataSetListApis apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public DataSetListApis apiVersionNumber(String apiVersionNumber) {
    this.apiVersionNumber = apiVersionNumber;
    return this;
  }

  public String getApiVersionNumber() {
    return apiVersionNumber;
  }

  public void setApiVersionNumber(String apiVersionNumber) {
    this.apiVersionNumber = apiVersionNumber;
  }

  public DataSetListApis apiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
    return this;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public DataSetListApis apiDocumentationUrl(String apiDocumentationUrl) {
    this.apiDocumentationUrl = apiDocumentationUrl;
    return this;
  }

  public String getApiDocumentationUrl() {
    return apiDocumentationUrl;
  }

  public void setApiDocumentationUrl(String apiDocumentationUrl) {
    this.apiDocumentationUrl = apiDocumentationUrl;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || getClass() != other.getClass()) return false;
    DataSetListApis o = (DataSetListApis) other;
    return Objects.equals(apiKey, o.apiKey)
        && Objects.equals(apiVersionNumber, o.apiVersionNumber)
        && Objects.equals(apiUrl, o.apiUrl)
        && Objects.equals(apiDocumentationUrl, o.apiDocumentationUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiKey, apiVersionNumber, apiUrl, apiDocumentationUrl);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", apiKey=").append(apiKey);
    builder.append(", apiVersionNumber=").append(apiVersionNumber);
    builder.append(", apiUrl=").append(apiUrl);
    builder.append(", apiDocumentationUrl=").append(apiDocumentationUrl);
    return builder.replace(0, 2, "DataSetListApis{").append('}').toString();
  }
}
