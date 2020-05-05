package restclients.client.java;

import restclients.client.ApiResponse;
import restclients.client.HttpMethod;
import restclients.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static restclients.client.HttpMethod.*;

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
    HttpURLConnection connection = openConnection(GET, url, headers);
    return getResponse(connection);
  }

  @Override
  public ApiResponse post(URL url, Map<String, String> headers, String body) throws IOException {
    HttpURLConnection connection = openConnection(POST, url, headers);
    setRequestBody(connection, body);
    return getResponse(connection);
  }

  @Override
  public ApiResponse delete(URL url, Map<String, String> headers, String body) throws IOException {
    HttpURLConnection connection = openConnection(DELETE, url, headers);
    setRequestBody(connection, body);
    return getResponse(connection);
  }

  private HttpURLConnection openConnection(HttpMethod method, URL url, Map<String, String> headers) throws IOException {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(method.name());
    if (headers != null) {
      headers.forEach(connection::setRequestProperty);
    }
    return connection;
  }

  private void setRequestBody(HttpURLConnection connection, String body) throws IOException {
    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);

    OutputStream os = connection.getOutputStream();
    os.write(body.getBytes(UTF_8));
    os.close();
  }

  private ApiResponse getResponse(HttpURLConnection connection) throws IOException {
    int code = connection.getResponseCode();
    String body = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
        .parallel().collect(joining("\n"));
    return new ApiResponse(code, body);
  }

}
