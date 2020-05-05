package restclients.client.java;

import restclients.client.ApiResponse;
import restclients.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static java.util.stream.Collectors.joining;

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
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    int code = connection.getResponseCode();
    String response = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
        .parallel().collect(joining("\n"));
    return new ApiResponse(code, response);
  }

  @Override
  public ApiResponse post(URL url, Map<String, String> headers, String body) {
    return null;
  }

  @Override
  public ApiResponse delete(URL url, Map<String, String> headers, String body) {
    return null;
  }

}
