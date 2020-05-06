package restclients.client.impl.spring;

import lombok.SneakyThrows;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import reactor.netty.http.HttpResources;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Optional.ofNullable;

/**
 * @author Yuriy Tumakha
 */
public class SpringWebClient implements RestClient {

    private final WebClient client = WebClient.create();

    @Override
    public String getName() {
        return "Spring Reactive WebClient";
    }

    @SneakyThrows
    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        RequestBodySpec requestSpec = client.method(org.springframework.http.HttpMethod.valueOf(method.name()))
                .uri(url.toURI());
        ofNullable(headers).ifPresent(map -> map.forEach(requestSpec::header));
        ofNullable(body).ifPresent(str -> requestSpec.body(BodyInserters.fromValue(str)));

        return requestSpec.exchange().toFuture()
                .thenCompose(r -> r.bodyToMono(String.class).toFuture()
                        .thenApply(str -> new ApiResponse(r.rawStatusCode(), str)));
    }

    @SneakyThrows
    @Override
    public void close() {
        HttpResources.disposeLoopsAndConnections();
    }

}
