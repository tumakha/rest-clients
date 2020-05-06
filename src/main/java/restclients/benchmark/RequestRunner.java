package restclients.benchmark;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import restclients.benchmark.model.TimeStats;
import restclients.client.model.ApiRequest;
import restclients.client.model.ApiResponse;
import restclients.client.RestClient;
import restclients.concurrent.ConcurrentRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Component
@Data
@Slf4j
public class RequestRunner {

  private static final double NANO_TO_MILLI = 1e6;

  private RestClient restClient;

  private ApiResponse sendRequest(ApiRequest r) throws IOException {
    switch (r.getMethod()) {
      case GET:
        return restClient.get(r.getUrl(), r.getHeaders());
      case POST:
        return restClient.post(r.getUrl(), r.getHeaders(), r.getBody());
      case DELETE:
        return restClient.delete(r.getUrl(), r.getHeaders(), r.getBody());
      default:
        throw new IllegalArgumentException("Unsupported HTTP method " + r.getMethod());
    }
  }

  public TimeStats run(ApiRequest r, int threads, int requestsNum) {
    final List<Long> clientTime = Collections.synchronizedList(new ArrayList<>());

    long startTestTime = System.nanoTime();

    new ConcurrentRun(threads, requestsNum, () -> {
      long startRequestTime = System.nanoTime();

      try {
        ApiResponse response = sendRequest(r);
      } catch (Exception e) {
        log.error("Request failed", e);
      }

      clientTime.add(System.nanoTime() - startRequestTime);
    });

    long totalTime = (System.nanoTime() - startTestTime) / (long) NANO_TO_MILLI;
    long avgPerRequest = totalTime / requestsNum;
    double avg = clientTime.stream().mapToLong(l -> l).average().getAsDouble() / NANO_TO_MILLI;
    double min = clientTime.stream().mapToLong(l -> l).min().getAsLong() / NANO_TO_MILLI;
    double max = clientTime.stream().mapToLong(l -> l).max().getAsLong() / NANO_TO_MILLI;

    System.out.println(String.format("%s %d requests by %d threads in %d ms = Average time per request: %d ms. " +
            "Request time (ms): min = %.3f, avg = %.3f, max = %.3f",
        r.getName(), requestsNum, threads, totalTime, avgPerRequest, min, avg, max));
    return new TimeStats(totalTime, avgPerRequest, min, avg, max);
  }

}
