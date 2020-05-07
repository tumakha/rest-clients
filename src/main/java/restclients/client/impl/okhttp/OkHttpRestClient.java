package restclients.client.impl.okhttp;

import lombok.SneakyThrows;
import okhttp3.*;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static restclients.client.model.HttpMethod.GET;

/**
 * @author Yuriy Tumakha
 */
public class OkHttpRestClient implements RestClient {

  private final OkHttpClient client = new OkHttpClient();

  @Override
  public String getName() {
    return "OkHttp";
  }

  @SneakyThrows
  @Override
  public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
    Request request = buildRequest(method, url, headers, body);
    try (Response response = client.newCall(request).execute()) {
      return toApiResponse(response);
    }
  }

  @Override
  public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
    Request request = buildRequest(method, url, headers, body);
    return executeAsync(request).thenApply(this::toApiResponse);
  }

  private CompletableFuture<Response> executeAsync(Request request) {
    final CompletableFuture<Response> completableFuture = new CompletableFuture<>();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(okhttp3.Call call, IOException e) {
        completableFuture.completeExceptionally(e);
      }

      @Override
      public void onResponse(Call call, Response response) {
        completableFuture.complete(response);
      }
    });
    return completableFuture;
  }

  private Request buildRequest(HttpMethod method, URL url, Map<String, String> headers, String body) {
    Request.Builder requestBuilder = new Request.Builder().url(url);
    if (method == GET) {
      requestBuilder.get();
    } else {
      String contentType = ofNullable(headers)
          .flatMap(map -> ofNullable(map.get("Content-Type"))).orElse("text/plain");
      requestBuilder.method(method.name(), RequestBody.create(body, MediaType.parse(contentType)));
    }
    ofNullable(headers).ifPresent(map -> map.forEach(requestBuilder::header));

    return requestBuilder.build();
  }

  @SneakyThrows
  private ApiResponse toApiResponse(Response response) {
    try (response; ResponseBody responseBody = requireNonNull(response.body())) {
      return new ApiResponse(response.code(), responseBody.string());
    }
  }

  @SneakyThrows
  @Override
  public void close() {
    client.dispatcher().executorService().shutdown();
    client.connectionPool().evictAll();
  }

}
