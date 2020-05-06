package restclients.client;

import restclients.client.model.ApiResponse;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * @author Yuriy Tumakha
 */
public interface RestClient {

  String getName();

  ApiResponse get(URL url, Map<String, String> headers) throws IOException;

  ApiResponse post(URL url, Map<String, String> headers, String body) throws IOException;

  ApiResponse delete(URL url, Map<String, String> headers, String body) throws IOException;

}
