package restclients.client.impl.apache;

import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import restclients.client.model.ApiResponse;
import restclients.client.model.HttpMethod;

import java.net.URL;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

/**
 * @author Yuriy Tumakha
 */
public interface ApacheClient {

  @SneakyThrows
  default HttpUriRequest buildRequest(HttpMethod method, URL url, Map<String, String> headers, String body) {
    RequestBuilder requestBuilder = RequestBuilder.create(method.name()).setUri(url.toURI());

    ofNullable(headers).ifPresent(map -> map.forEach(requestBuilder::setHeader));
    if (body != null) {
      requestBuilder.setEntity(new StringEntity(body, UTF_8));
    }
    return requestBuilder.build();
  }

  @SneakyThrows
  default ApiResponse toApiResponse(HttpResponse response) {
    String content = EntityUtils.toString(response.getEntity(), UTF_8);
    return new ApiResponse(response.getStatusLine().getStatusCode(), content);
  }

}
