package restclients.client.impl.jetty;

import lombok.SneakyThrows;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

/**
 * @author Yuriy Tumakha
 */
public class JettyHttpClient implements RestClient {

  private final HttpClient client;

  public JettyHttpClient() throws Exception {
    SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
    client = new HttpClient(sslContextFactory);
    client.setFollowRedirects(false);
    client.start();
  }

  @Override
  public String getName() {
    return "Jetty";
  }

  @SneakyThrows
  @Override
  public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
    Request request = buildRequest(method, url, headers, body);
    ContentResponse response = request.send();
    return toApiResponse(response);
  }

  @SneakyThrows
  @Override
  public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
    Request request = buildRequest(method, url, headers, body);

    final CompletableFuture<ApiResponse> completableFuture = new CompletableFuture<>();
    request
        .onRequestFailure((req, ex) -> completableFuture.completeExceptionally(ex))
        .onResponseFailure((req, ex) -> completableFuture.completeExceptionally(ex))
        .onResponseContent((response, buffer) -> completableFuture.complete(toApiResponse(response, buffer)))
        .send(result -> {});
    return completableFuture;
  }

  private Request buildRequest(HttpMethod method, URL url, Map<String, String> headers, String body) throws URISyntaxException {
    Request request = client.newRequest(url.toURI())
        .method(org.eclipse.jetty.http.HttpMethod.valueOf(method.name()));

    ofNullable(headers).ifPresent(map -> map.forEach(request::header));
    ofNullable(body).ifPresent(str -> {
      String contentType = ofNullable(headers)
          .flatMap(map -> ofNullable(map.get("Content-Type"))).orElse("text/plain");
      request.content(new StringContentProvider(contentType, body, UTF_8));
    });
    return request;
  }

  @SneakyThrows
  private ApiResponse toApiResponse(Response response, ByteBuffer content) {
    String body = content.hasArray() ? new String(content.array(), UTF_8) : "";
    return new ApiResponse(response.getStatus(), body);
  }

  @SneakyThrows
  private ApiResponse toApiResponse(ContentResponse response) {
    return new ApiResponse(response.getStatus(), response.getContentAsString());
  }

  @SneakyThrows
  @Override
  public void close() {
    client.stop();
  }

}
