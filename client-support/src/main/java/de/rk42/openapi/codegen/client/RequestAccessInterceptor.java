package de.rk42.openapi.codegen.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * OkHttp interceptor for accessing the final request. This is necessary, because the application can use interceptors that add or modify headers, and
 * we want to report the final set of headers used.
 */
class RequestAccessInterceptor implements Interceptor {

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
