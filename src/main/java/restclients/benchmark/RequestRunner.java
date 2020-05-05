package restclients.benchmark;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import restclients.client.ApiRequest;
import restclients.client.ApiResponse;
import restclients.client.RestClient;
import restclients.concurrent.ConcurrentRun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * @author Yuriy Tumakha
 */
@Component
@Data
@Slf4j
public class RequestRunner {

  private RestClient restClient;

  public TimeStats run(ApiRequest request, int threads, int requests) {
    List<Long> clientTime = Collections.synchronizedList(new ArrayList<>());

    long startTestTime = System.nanoTime();

    new ConcurrentRun(threads, requests, () -> {
      long startRequestTime = System.nanoTime();

      try {
        ApiResponse response = restClient.sendRequest(request);
        if (response.getCode() >= 400) {
          log.warn("{} returns {}: {}", request.getUrl(), response.getCode(), response.getBody());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      clientTime.add(System.nanoTime() - startRequestTime);
    });

    double nanoToMilli = 1e6;

    long totalTime = (System.nanoTime() - startTestTime) / (long) nanoToMilli;
    double avgPerRequest = totalTime / (double) requests;

    log.debug(String.format("\n%d threads. %d parallel requests in %d ms. Average time per request: %.3f ms",
        threads, requests, totalTime, avgPerRequest));

    double avg = clientTime.stream().filter(Objects::nonNull).mapToLong(l -> l).average().getAsDouble() / nanoToMilli;
    double min = clientTime.stream().filter(Objects::nonNull).mapToLong(l -> l).min().getAsLong() / nanoToMilli;
    double max = clientTime.stream().filter(Objects::nonNull).mapToLong(l -> l).max().getAsLong() / nanoToMilli;

    System.out.println(String.format("Request time on CLIENT (ms): min = %.3f, avg = %.3f, max = %.3f", min, avg, max));
    return new TimeStats(totalTime, avgPerRequest, min, avg, max);
  }

}
