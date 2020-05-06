package restclients.client;

import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.io.Closeable;
import java.net.URL;
import java.util.Map;

import static restclients.client.model.HttpMethod.*;

/**
 * @author Yuriy Tumakha
 */
public interface RestClient extends Closeable {

    String getName();

    ApiResponse send(HttpMethod method, URL url, Map<String, String> headers, String body);

    default ApiResponse get(URL url, Map<String, String> headers) {
        return send(GET, url, headers, null);
    }

    default ApiResponse post(URL url, Map<String, String> headers, String body) {
        return send(POST, url, headers, body);
    }

    default ApiResponse delete(URL url, Map<String, String> headers, String body) {
        return send(DELETE, url, headers, body);
    }

    default void close() {
    }

}
