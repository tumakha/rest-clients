package restclients.client.java;

import restclients.client.ApiRequest;
import restclients.client.ApiResponse;
import restclients.client.RestClient;

/**
 * @author Yuriy Tumakha
 */
public class SimpleJavaClient implements RestClient {

  @Override
  public String getName() {
    return "Java HttpURLConnection";
  }

  @Override
  public ApiResponse sendRequest(ApiRequest request) {
    return null;
  }
}
