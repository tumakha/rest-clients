package restclients.client.model;

import lombok.Value;

import java.net.URL;
import java.util.Map;

/**
 * @author Yuriy Tumakha
 */
@Value
public class ApiRequest {
  HttpMethod method;
  URL url;
  Map<String, String> headers;
  String body;

  public String getName() {
    return method + " " + url.getHost();
  }

}
