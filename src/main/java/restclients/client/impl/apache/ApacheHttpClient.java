package restclients.client.impl.apache;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Yuriy Tumakha
 */
public class ApacheHttpClient implements RestClient, ApacheClient {

    private final CloseableHttpClient client = HttpClients.createDefault();

    @Override
    public String getName() {
        return "Apache HttpClient";
    }

    @SneakyThrows
    @Override
    public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
        final HttpUriRequest request = buildRequest(method, url, headers, body);

        try (CloseableHttpResponse response = client.execute(request)) {
            return toApiResponse(response);
        }
    }

    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        return CompletableFuture.supplyAsync(() -> send(method, url, headers, body));
    }

    @SneakyThrows
    @Override
    public void close() {
        client.close();
    }

}
