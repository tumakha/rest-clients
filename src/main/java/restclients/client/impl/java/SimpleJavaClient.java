package restclients.client.impl.java;

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

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static restclients.client.model.HttpMethod.*;

/**
 * @author Yuriy Tumakha
 */
public class SimpleJavaClient implements RestClient {

    @Override
    public String getName() {
        return "Java HttpURLConnection";
    }

    @Override
    public ApiResponse get(URL url, Map<String, String> headers) throws IOException {
        return send(GET, url, headers, null);
    }

    @Override
    public ApiResponse post(URL url, Map<String, String> headers, String body) throws IOException {
        return send(POST, url, headers, body);
    }

    @Override
    public ApiResponse delete(URL url, Map<String, String> headers, String body) throws IOException {
        return send(DELETE, url, headers, body);
    }

    private ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        ofNullable(headers).ifPresent(map -> map.forEach(connection::setRequestProperty));
        ofNullable(body).ifPresent(str -> setRequestBody(connection, str));
        return getResponse(connection);
    }

    private void setRequestBody(HttpURLConnection connection, String body) {
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes(UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
