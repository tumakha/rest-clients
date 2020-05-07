package restclients.client.impl.apache;

import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Yuriy Tumakha
 */
public class ApacheHttpAsyncClient implements RestClient, ApacheClient {

    private final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();

    public ApacheHttpAsyncClient() {
        client.start();
    }

    @Override
    public String getName() {
        return "Apache HttpAsyncClient";
    }

    @SneakyThrows
    public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
        final HttpUriRequest request = buildRequest(method, url, headers, body);

        HttpResponse response = client.execute(request, null).get();
        return toApiResponse(response);
    }

    @SneakyThrows
    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        final HttpUriRequest request = buildRequest(method, url, headers, body);

        final CompletableFuture<HttpResponse> completableFuture = new CompletableFuture<>();
        client.execute(request, new FutureCallback<>() {
            public void completed(final HttpResponse response) {
                completableFuture.complete(response);
            }

            public void failed(final Exception ex) {
                completableFuture.completeExceptionally(ex);
            }

            public void cancelled() {
                completableFuture.completeExceptionally(new IOException(request.getRequestLine() + " cancelled"));
            }
        });
        return completableFuture.thenApply(this::toApiResponse);
    }

    @SneakyThrows
    @Override
    public void close() {
        client.close();
    }

}
