package restclients.client.impl.java;

import lombok.SneakyThrows;
import restclients.client.RestClient;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

/**
 * @author Yuriy Tumakha
 */
public class SimpleJavaClient implements RestClient {

    @Override
    public String getName() {
        return "Java HttpURLConnection";
    }

    @SneakyThrows
    @Override
    public ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        ofNullable(headers).ifPresent(map -> map.forEach(connection::setRequestProperty));
        ofNullable(body).ifPresent(str -> setRequestBody(connection, str));
        return getResponse(connection);
    }

    @Override
    public CompletableFuture<ApiResponse> sendAsync(HttpMethod method, URL url, Map<String, String> headers, String body) {
        return CompletableFuture.supplyAsync(() -> send(method, url, headers, body));
    }

    @SneakyThrows
    private void setRequestBody(HttpURLConnection connection, String body) {
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes(UTF_8));
        }
    }

    private ApiResponse getResponse(HttpURLConnection connection) throws IOException {
        int code = connection.getResponseCode();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
            String body = reader.lines().collect(joining(lineSeparator()));
            return new ApiResponse(code, body);
        }
    }

}
