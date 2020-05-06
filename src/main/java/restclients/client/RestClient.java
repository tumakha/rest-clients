package restclients.client;

import lombok.SneakyThrows;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.Closeable;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static restclients.client.model.HttpMethod.*;

/**
 * @author Yuriy Tumakha
 */
public interface RestClient extends Closeable {

    Duration TIMEOUT = ofSeconds(10);

    String getName();

    CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body);

    @SneakyThrows
    default ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
        CompletableFuture<ApiResponse> completableFuture = sendAsync(method, url, headers, body);
        completableFuture.exceptionally(e -> {
            throw new RuntimeException(e);
        });
        return completableFuture.get(TIMEOUT.getSeconds(), SECONDS);
    }

    default ApiResponse get(URL url, Map<String, String> headers) {
        return send(GET, url, headers, null);
    }

    default ApiResponse post(URL url, Map<String, String> headers, String body) {
        return send(POST, url, headers, body);
    }

    default ApiResponse delete(URL url, Map<String, String> headers, String body) {
        return send(DELETE, url, headers, body);
    }

    default CompletableFuture<ApiResponse> getAsync(URL url, Map<String, String> headers) {
        return sendAsync(GET, url, headers, null);
    }

    default CompletableFuture<ApiResponse> postAsync(URL url, Map<String, String> headers, String body) {
        return sendAsync(POST, url, headers, body);
    }

    default CompletableFuture<ApiResponse> deleteAsync(URL url, Map<String, String> headers, String body) {
        return sendAsync(DELETE, url, headers, body);
    }

    default void close() {
    }

}
