package io.github.ruedigerk.contractfirst.generator.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.OperationRequestBody;
import io.github.ruedigerk.contractfirst.generator.client.internal.Parameter;
import io.github.ruedigerk.contractfirst.generator.client.internal.ResponseDefinition;
import io.github.ruedigerk.contractfirst.generator.support.gson.LocalDateGsonTypeAdapter;
import io.github.ruedigerk.contractfirst.generator.support.gson.OffsetDateTimeGsonTypeAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpMethod;

/**
 * Performs HTTP requests as defined by generated client code.
 */
public class RequestExecutor {

  private static final String CONTENT_TYPE_HEADER = "Content-Type";

  private final Gson gson;
  private final OkHttpClient httpClient;
  private final String baseUrl;

  /**
   * Constructs a new instance of RequestExecutor. Instances are thread-safe and can be shared across multiple instances of generated ApiClients.
   *
   * @param httpClient the OkHttp-Client instance to use for sending HTTP requests.
   * @param baseUrl    the base URL to send requests to.
   */
  public RequestExecutor(OkHttpClient httpClient, String baseUrl) {
    this.httpClient = addInternalInterceptors(httpClient);
    this.baseUrl = removeTrailingSlash(baseUrl);

    gson = createGson();
  }

  /**
   * Returns the Gson instance used to serialize and deserialize JSON entities.
   */
  public Gson getGson() {
    return gson;
  }

  /**
   * Returns the OkHttpClient instance used to send requests.
   */
  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * Returns the base URL used to send requests.
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  private static <T> T firstNonNull(T first, T second) {
    return first != null ? first : Objects.requireNonNull(second);
  }

  private static List<Header> extractHeaders(Headers headers) {
    return StreamSupport.stream(headers.spliterator(), false)
        .map(pair -> new Header(pair.getFirst(), pair.getSecond()))
        .collect(Collectors.toList());
  }

  private OkHttpClient addInternalInterceptors(OkHttpClient httpClient) {
    return httpClient.newBuilder()
        .addNetworkInterceptor(new RequestAccessInterceptor())
        .build();
  }

  private String removeTrailingSlash(String baseUrl) {
    return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
  }

  private Gson createGson() {
    return new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateGsonTypeAdapter())
        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeGsonTypeAdapter())
        .create();
  }

  public ApiResponse executeRequest(Operation operation)
      throws ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {
    validateOperation(operation);

    Request request = createRequest(operation);
    RequestAndResponse requestAndResponse = executeHttpRequest(request);

    return interpretResponse(requestAndResponse, operation);
  }

  private void validateOperation(Operation operation) throws ApiClientValidationException {
    if (operation.getRequestBody().isRequired() && operation.getRequestBody().getEntity() == null) {
      throw new ApiClientValidationException("Request body is required but missing");
    }

    for (Parameter parameter : operation.getParameters()) {
      if (parameter.isRequired() && parameter.getValue() == null) {
        throw new ApiClientValidationException("Parameter " + parameter + " is required but missing");
      }
    }
  }

  private RequestAndResponse executeHttpRequest(Request request) {
    try {
      Response response = httpClient.newCall(request).execute();
      // RequestAccessInterceptor.getLastRequest() is used instead of response.request() because the latter is missing all headers that OkHttp is adding
      // late in the request processing, like Content-Type, Content-Size, etc.
      return new RequestAndResponse(RequestAccessInterceptor.getLastRequest(), response);
    } catch (IOException e) {
      // Special case for extremely short request timeouts, where the request times out even before the RequestAccessInterceptor was called
      Request detailedRequest = firstNonNull(RequestAccessInterceptor.getLastRequest(), request);
      ApiRequest apiRequest = toApiRequest(detailedRequest);
      throw new ApiClientIoException("Error executing request: " + e, apiRequest, e);
    } finally {
      RequestAccessInterceptor.clearThreadLocal();
    }
  }

  private ApiRequest toApiRequest(Request request) {
    List<Header> headers = extractHeaders(request.headers());
    return new ApiRequest(request.url().toString(), request.method(), headers);
  }

  public Request createRequest(Operation operation) throws ApiClientIoException {
    HttpUrl url = determineRequestUrl(operation);
    Headers headers = determineRequestHeaders(operation);

    try {
      RequestBody requestBody = serializeRequestBody(operation);

      return new Request.Builder()
          .url(url)
          .method(operation.getMethod(), requestBody)
          .headers(headers)
          .build();
    } catch (IOException e) {
      ApiRequest apiRequest = new ApiRequest(url.toString(), operation.getMethod(), extractHeaders(headers));
      throw new ApiClientIoException("Error serializing request body: " + e, apiRequest, e);
    }
  }

  private HttpUrl determineRequestUrl(Operation operation) {
    String path = determineRequestPath(operation);
    Builder urlBuilder = HttpUrl.get(baseUrl + path).newBuilder();

    operation.getQueryParameters().forEach((name, parameter) -> {
      if (parameter.getValue() != null) {
        String value = serializeParameter(parameter.getValue());
        urlBuilder.addQueryParameter(name, value);
      }
    });

    return urlBuilder.build();
  }

  private String determineRequestPath(Operation operation) {
    String path = operation.getPath();

    for (Parameter parameter : operation.getPathParameters().values()) {
      String placeholder = "{" + parameter.getName() + "}";
      String value = serializeParameter(parameter.getValue());

      path = path.replace(placeholder, value);
    }

    return path;
  }

  // This method does not need to set a Content-Type header, as that is done by OkHttp when we set the media-type on the request body.
  private Headers determineRequestHeaders(Operation operation) {
    Headers.Builder builder = new Headers.Builder();

    operation.getHeaderParameters().forEach((name, parameter) -> {
      if (parameter.getValue() != null) {
        String value = serializeParameter(parameter.getValue());
        builder.add(name, value);
      }
    });

    String acceptHeaderValue = determineAcceptHeaderValue(operation);
    if (!acceptHeaderValue.isEmpty()) {
      builder.add("Accept", acceptHeaderValue);
    }

    return builder.build();
  }

  /**
   * All JSON-compatible mime types are sent with a q-factor of 1 and all other mime types with a q-factor of 0.5.
   */
  // See: https://developer.mozilla.org/en-US/docs/Glossary/Quality_values
  // See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept
  // See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Content_negotiation
  // TODO: Write unit test
  private String determineAcceptHeaderValue(Operation operation) {
    Map<Boolean, List<String>> partitionedMediaTypes = operation.getAllAcceptedMediaTypes()
        .stream()
        .collect(Collectors.partitioningBy(this::isJsonMediaType));

    List<String> jsonMediaTypes = partitionedMediaTypes.get(true);
    List<String> nonJsonMediaTypes = partitionedMediaTypes.get(false);

    return Stream.concat(
        jsonMediaTypes.stream(),
        nonJsonMediaTypes.stream().map(mediaType -> mediaType + "; q=0.5")
    ).collect(Collectors.joining(", "));
  }

  private String serializeParameter(Object value) {
    if (value == null) {
      return "";
    } else {
      // LocalDate and OffsetDateTime already return the desired format in their toString methods.
      return value.toString();
    }
  }

  private RequestBody serializeRequestBody(Operation operation) throws IOException {
    OperationRequestBody requestBody = operation.getRequestBody();

    if (requestBody.getEntity() == null) {
      if (HttpMethod.requiresRequestBody(operation.getMethod())) {
        return createEmptyRequestBody();
      } else {
        return null;
      }
    }

    Object entity = requestBody.getEntity();
    MediaType mediaType = MediaType.get(requestBody.getContentType());

    if (isJsonMediaType(mediaType)) {
      String content = gson.toJson(entity);
      return RequestBody.create(content, mediaType);
    } else if (entity instanceof byte[]) {
      return RequestBody.create((byte[]) entity, mediaType);
    } else if (entity instanceof InputStream) {
      byte[] bytes = readBytes((InputStream) entity);
      return RequestBody.create(bytes, mediaType);
    } else {
      // Not a JSON media type, so we assume it is text.
      String content = requestBody.toString();
      return RequestBody.create(content, mediaType);
    }
  }

  private RequestBody createEmptyRequestBody() {
    return RequestBody.create(new byte[0], null);
  }

  private byte[] readBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[4096];

    try (InputStream input = inputStream) {
      int bytesRead;
      while ((bytesRead = input.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    }

    // Closing a ByteArrayOutputStream has no effect, so we don't do it.
    return outputStream.toByteArray();
  }

  /**
   * Interprets the response from the server and returns the appropriate Response or throws an ApiClientIoException in case of an IOException.
   *
   * Note: this method closes the response body unless it returns an InputStream of the body!
   *
   * @throws ApiClientIoException                   when an IOException occurs reading the response.
   * @throws ApiClientIncompatibleResponseException when the response is not conforming to the specification of the API.
   */
  private ApiResponse interpretResponse(RequestAndResponse requestAndResponse, Operation operation)
      throws ApiClientIoException, ApiClientIncompatibleResponseException {

    Request request = requestAndResponse.request;
    Response response = requestAndResponse.response;
    ResponseBody responseBody = Objects.requireNonNull(response.body());

    int statusCode = response.code();
    String mediaType = response.header(CONTENT_TYPE_HEADER);

    ApiRequest apiRequest = toApiRequest(request);
    ResponseBuilder responseBuilder = new ResponseBuilder(apiRequest, statusCode, response.message(), mediaType, response.headers());

    try {
      boolean isEmptyBody = responseBody.source().exhausted();
      Type javaType = determineTypeOfContent(statusCode, mediaType, isEmptyBody, operation);

      if (javaType == null) {
        // The response is not described in the contract, return an unexpected response.
        String bodyContent = responseBody.string();
        response.close();
        IncompatibleResponse incompatibleResponse = responseBuilder.incompatibleResponse(bodyContent);
        throw new ApiClientIncompatibleResponseException("The combination of the response's status code and content type is unknown", incompatibleResponse);
      } else if (javaType.equals(Void.TYPE)) {
        // The response should be empty according to the contract. Ignore body if exists.
        response.close();
        return responseBuilder.apiResponse(javaType, null);
      } else if (javaType.equals(InputStream.class)) {
        // The contract says to return the body unprocessed, i.e., as type InputStream.
        // In this case, the application is responsible for closing the InputStream!
        return responseBuilder.apiResponse(javaType, responseBody.byteStream());
      } else if (isJsonMediaType(mediaType)) {
        // The contract defines the response to be a JSON entity. Deserialize and return it.
        // Note: deserializeFromJson closes the response body.
        return deserializeFromJson(responseBuilder, responseBody, javaType);
      } else if (javaType.equals(String.class)) {
        // The contract defines the response to be some string content, e.g., text/plain, so just return it as a string.
        return responseBuilder.apiResponse(javaType, responseBody.string());
      } else {
        // In this case, the contract defines a schema for the response, but the server sends it in an unsupported format, i.e., a non-JSON format.
        String bodyContent = responseBody.string();
        IncompatibleResponse incompatibleResponse = responseBuilder.incompatibleResponse(bodyContent);
        throw new ApiClientIncompatibleResponseException("Content-Type not supported by API client: " + mediaType, incompatibleResponse);
      }
    } catch (IOException e) {
      // The body could not be read
      response.close();
      IncompleteResponse incompleteResponse = responseBuilder.incompleteResponse();
      throw new ApiClientIoException("Error reading response body: " + e, incompleteResponse, e);
    }
  }

  /**
   * Returns the Java type of the response. If the response is not matching the contract, null is returned. If the response is defined to have no content, type
   * {@link Void#TYPE} is returned.
   */
  // TODO: Write unit test
  private Type determineTypeOfContent(int statusCode, String contentType, boolean isEmptyBody, Operation operation) {
    // If the contentType is null, the server should not have sent a response body. If it has nevertheless, something is amiss.
    if (contentType == null && !isEmptyBody) {
      return null;
    }

    MediaType mediaType = contentType == null ? null : MediaType.get(contentType);
    List<ResponseDefinition> responseDefinitions = operation.selectResponseDefinitionsByStatusCode(statusCode);

    for (ResponseDefinition definition : responseDefinitions) {
      if (mediaType == null && definition.hasNoContent()) {
        return Void.TYPE;
      } else if (mediaType != null && isCompatibleMediaType(mediaType, MediaType.get(definition.getContentType()))) {
        return definition.getJavaType();
      }
    }

    return null;
  }

  // TODO: Write unit test
  private boolean isCompatibleMediaType(MediaType testedMediaType, MediaType mediaTypeToMatchAgainst) {
    if (mediaTypeToMatchAgainst.type().equals("*")) {
      return true;
    }

    boolean sameType = mediaTypeToMatchAgainst.type().equals(testedMediaType.type());
    return sameType && (mediaTypeToMatchAgainst.subtype().equals("*") || mediaTypeToMatchAgainst.subtype().equals(testedMediaType.subtype()));
  }

  // TODO: Write unit test
  private boolean isJsonMediaType(String mediaType) {
    if (mediaType == null) {
      return false;
    }

    return isJsonMediaType(MediaType.get(mediaType));
  }

  // TODO: Write unit test
  private boolean isJsonMediaType(MediaType mediaType) {
    if (mediaType == null) {
      return false;
    }

    String subtype = mediaType.subtype();
    return mediaType.type().equals("application") && (subtype.equals("json") || subtype.startsWith("vnd.") && subtype.endsWith("+json"));
  }

  /**
   * Deserializes the response entity and closes the response body.
   */
  private ApiResponse deserializeFromJson(ResponseBuilder responseBuilder, ResponseBody responseBody, Type expectedType)
      throws IOException, ApiClientIncompatibleResponseException {

    String stringContent = readAndCloseResponseBody(responseBody);

    try {
      Object entity = gson.fromJson(stringContent, expectedType);
      return responseBuilder.apiResponse(expectedType, entity);
    } catch (JsonParseException e) {
      IncompatibleResponse incompatibleResponse = responseBuilder.incompatibleResponse(stringContent);
      throw new ApiClientIncompatibleResponseException("JSON response from server cannot be parsed to " + expectedType + ": " + e, incompatibleResponse, e);
    }
  }

  private String readAndCloseResponseBody(ResponseBody responseBody) throws IOException {
    try {
      return responseBody.string();
    } finally {
      responseBody.close();
    }
  }

  private static class RequestAndResponse {

    final Request request;
    final Response response;

    RequestAndResponse(Request request, Response response) {
      this.request = Objects.requireNonNull(request, "request");
      this.response = Objects.requireNonNull(response, "response");
    }
  }

  /**
   * Builder for constructing Responses, either defined or undefined.
   */
  private static class ResponseBuilder {

    private final ApiRequest request;
    private final int statusCode;
    private final String httpStatusMessage;
    private final String contentType;
    private final List<Header> headers;

    ResponseBuilder(ApiRequest request, int statusCode, String httpStatusMessage, String contentType, Headers headers) {
      this.request = request;
      this.statusCode = statusCode;
      this.httpStatusMessage = httpStatusMessage;
      this.contentType = contentType;
      this.headers = extractHeaders(headers);
    }

    ApiResponse apiResponse(Type javaType, Object entity) {
      return new ApiResponse(request, statusCode, httpStatusMessage, contentType, headers, entity, javaType);
    }

    IncompleteResponse incompleteResponse() {
      return new IncompleteResponse(request, statusCode, httpStatusMessage, contentType, headers);
    }

    IncompatibleResponse incompatibleResponse(String responseBody) {
      return new IncompatibleResponse(request, statusCode, httpStatusMessage, contentType, headers, responseBody);
    }
  }

  /**
   * OkHttp interceptor for accessing the final request. This is necessary, because the application can use interceptors that add or modify headers, and we want
   * to report the final set of headers used.
   */
  private static class RequestAccessInterceptor implements Interceptor {

    private static final ThreadLocal<Request> THREAD_LOCAL = new ThreadLocal<>();

    public static Request getLastRequest() {
      return THREAD_LOCAL.get();
    }

    public static void clearThreadLocal() {
      THREAD_LOCAL.remove();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      THREAD_LOCAL.set(request);

      return chain.proceed(request);
    }
  }
}
