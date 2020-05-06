package restclients.client.impl.java11;

import lombok.SneakyThrows;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static restclients.client.model.HttpMethod.GET;

/**
 * @author Yuriy Tumakha
 */
public class Java11HttpClient implements RestClient {

    private static final Duration TIMEOUT = ofSeconds(10);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final HttpClient client = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(TIMEOUT)
            .executor(executor)
            .build();

    @Override
    public String getName() {
        return "Java 11 HttpClient";
    }

    @SneakyThrows
    @Override
    public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(url.toURI()).timeout(TIMEOUT);
        ofNullable(headers).ifPresent(map -> map.forEach(requestBuilder::setHeader));

        if (method == GET) {
            requestBuilder.GET();
        } else if (body != null) {
            requestBuilder.method(method.name(), BodyPublishers.ofString(body, UTF_8));
        }
        HttpRequest request = requestBuilder.build();

        CompletableFuture<ApiResponse> completableFuture = client.sendAsync(request, BodyHandlers.ofString(UTF_8))
                .exceptionally(e -> {
                    throw new RuntimeException(e);
                })
                .thenApply(r -> new ApiResponse(r.statusCode(), r.body()));
        return completableFuture.get(TIMEOUT.getSeconds(), SECONDS);
    }

    @SneakyThrows
    @Override
    public void close() {
        executor.shutdown();
        executor.awaitTermination(50, SECONDS);
    }

}
