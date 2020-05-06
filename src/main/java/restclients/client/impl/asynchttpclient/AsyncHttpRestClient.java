package restclients.client.impl.asynchttpclient;

import lombok.SneakyThrows;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Optional.ofNullable;
import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * @author Yuriy Tumakha
 */
public class AsyncHttpRestClient implements RestClient {

    private static final int TIMEOUT_MILLISECONDS = 10_000;

    private final AsyncHttpClient asyncHttpClient = asyncHttpClient();

    @Override
    public String getName() {
        return "AsyncHttpClient";
    }

    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        BoundRequestBuilder requestBuilder = asyncHttpClient
                .prepare(method.name(), url.toString())
                .setRequestTimeout(TIMEOUT_MILLISECONDS)
                .setReadTimeout(TIMEOUT_MILLISECONDS);

        ofNullable(headers).ifPresent(map -> map.forEach(requestBuilder::setHeader));
        ofNullable(body).ifPresent(requestBuilder::setBody);

        return requestBuilder.execute().toCompletableFuture()
                .thenApply(r -> new ApiResponse(r.getStatusCode(), r.getResponseBody()));
    }

    @SneakyThrows
    @Override
    public void close() {
        asyncHttpClient.close();
    }

}
