package restclients.client.impl.akka;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.stream.Materializer;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.model.ContentTypes.APPLICATION_JSON;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Yuriy Tumakha
 */
public class AkkaRestClient implements RestClient {

  private final ActorSystem system = ActorSystem.create();
  private final Http http = Http.get(system);

  @Override
  public String getName() {
    return "Akka HTTP";
  }

  @Override
  public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
    HttpRequest request = buildRequest(method, url, headers, body);

    CompletionStage<HttpResponse> completionStage = http.singleRequest(request);
    return completionStage
        .thenCompose(this::toApiResponse)
        .toCompletableFuture();
  }

  private HttpRequest buildRequest(HttpMethod method, URL url, Map<String, String> headers, String body) {
    HttpRequest request = createHttpRequest(method, url);
    if (headers != null) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        if (!header.getKey().equals("Content-Type")) {
          request = request.addHeader(HttpHeader.parse(header.getKey(), header.getValue()));
        }
      }
    }
    if (body != null) {
      request = request.withEntity(HttpEntities.create(APPLICATION_JSON, body));
    }
    return request;
  }

  private HttpRequest createHttpRequest(HttpMethod method, URL url) {
    switch (method) {
      case GET:
        return HttpRequest.GET(url.toString());
      case POST:
        return HttpRequest.POST(url.toString());
      case DELETE:
        return HttpRequest.DELETE(url.toString());
      default:
        throw new IllegalArgumentException("Unsupported HTTP method " + method);
    }
  }

  private CompletionStage<ApiResponse> toApiResponse(HttpResponse response) {
    return response.entity().toStrict(9_000, Materializer.matFromSystem(system))
        .thenApply(entity -> {
          String body = entity.getData().decodeString(UTF_8);
          return new ApiResponse(response.status().intValue(), body);
        });
  }

  @Override
  public void close() {
    system.terminate();
  }

}
