package restclients.benchmark;

import org.springframework.stereotype.Component;
import restclients.client.ApiRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static restclients.client.HttpMethod.*;

/**
 * @author Yuriy Tumakha
 */
@Component
public class Scenario {

  public List<ApiRequest> getRequests() throws MalformedURLException {
    Map<String, String> headers = Map.of("Accept", "application/json");

    return List.of(
        new ApiRequest(GET, new URL("https://postman-echo.com/get?foo1=bar1&foo2=bar2"), headers, null),
        new ApiRequest(POST, new URL("https://postman-echo.com/post"), headers, "{\"post\": \"body\"}"),
        new ApiRequest(DELETE, new URL("https://www.upwork.com/api"), null, "{\"delete\": \"body\"}")
    );
  }

}
