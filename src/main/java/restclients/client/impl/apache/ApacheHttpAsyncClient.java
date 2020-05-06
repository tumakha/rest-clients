package restclients.client.impl.apache;

import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

/**
 * @author Yuriy Tumakha
 */
public class ApacheHttpAsyncClient implements RestClient {

    private final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();

    public ApacheHttpAsyncClient() {
        client.start();
    }

    @Override
    public String getName() {
        return "Apache HttpAsyncClient";
    }

    @SneakyThrows
    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        RequestBuilder requestBuilder = requestBuilder(method, url);

        ofNullable(headers).ifPresent(map -> map.forEach(requestBuilder::setHeader));
        if (body != null) {
            requestBuilder.setEntity(new StringEntity(body, UTF_8));
        }

        final HttpUriRequest request = requestBuilder.build();

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
        return completableFuture
                .thenApply(r -> {
                    try {
                        String content = EntityUtils.toString(r.getEntity(), UTF_8);
                        return new ApiResponse(r.getStatusLine().getStatusCode(), content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @SneakyThrows
    @Override
    public void close() {
        client.close();
    }

    @SneakyThrows
    private RequestBuilder requestBuilder(HttpMethod method, URL url) {
        switch (method) {
            case GET:
                return RequestBuilder.get(url.toURI());
            case POST:
                return RequestBuilder.post(url.toURI());
            case DELETE:
                return RequestBuilder.delete(url.toURI());
            default:
                throw new UnsupportedOperationException("Unsupported HTTP method " + method);
        }
    }

}
