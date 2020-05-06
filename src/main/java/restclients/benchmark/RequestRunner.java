package restclients.benchmark;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import restclients.benchmark.model.TimeStats;
import restclients.client.RestClient;
import restclients.client.model.ApiRequest;
import restclients.client.model.ApiResponse;
import restclients.concurrent.ConcurrentRun;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.synchronizedList;

/**
 * @author Yuriy Tumakha
 */
@Component
@Data
@Slf4j
public class RequestRunner {

  private static final double NANO_TO_MILLI = 1e6;

  private RestClient restClient;

  private ApiResponse sendRequest(ApiRequest r) {
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

  private CompletableFuture<ApiResponse> sendRequestAsync(ApiRequest r) {
    switch (r.getMethod()) {
      case GET:
        return restClient.getAsync(r.getUrl(), r.getHeaders());
      case POST:
        return restClient.postAsync(r.getUrl(), r.getHeaders(), r.getBody());
      case DELETE:
        return restClient.deleteAsync(r.getUrl(), r.getHeaders(), r.getBody());
      default:
        throw new IllegalArgumentException("Unsupported HTTP method " + r.getMethod());
    }
  }

  public TimeStats run(ApiRequest r, int threads, int requestsNum) {
    final List<Long> clientTime = synchronizedList(new ArrayList<>());

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

  public TimeStats runNonBlocking(ApiRequest r, int threads, int requestsNum) {
    final List<Long> clientTime = synchronizedList(new ArrayList<>());
    final List<CompletableFuture<ApiResponse>> futures = synchronizedList(new ArrayList<>());

    long startTestTime = System.nanoTime();

    new ConcurrentRun(threads, requestsNum, () -> {
      long startRequestTime = System.nanoTime();

      try {
        CompletableFuture<ApiResponse> future = sendRequestAsync(r);
        future.thenRun(() -> clientTime.add(System.nanoTime() - startRequestTime));
        futures.add(future);
      } catch (Exception e) {
        log.error("Request failed", e);
      }

    });

    futures.forEach(CompletableFuture::join); // wait completion of all futures

    long totalTime = (System.nanoTime() - startTestTime) / (long) NANO_TO_MILLI;
    long avgPerRequest = totalTime / requestsNum;
    double avg = clientTime.stream().mapToLong(l -> l).average().getAsDouble() / NANO_TO_MILLI;
    double min = clientTime.stream().mapToLong(l -> l).min().getAsLong() / NANO_TO_MILLI;
    double max = clientTime.stream().mapToLong(l -> l).max().getAsLong() / NANO_TO_MILLI;

    System.out.println(String.format("%s Non-blocking %d requests by %d threads in %d ms = per request: %d ms. " +
                    "Request time (ms): min = %.3f, avg = %.3f, max = %.3f",
            r.getName(), requestsNum, threads, totalTime, avgPerRequest, min, avg, max));
    return new TimeStats(totalTime, avgPerRequest, min, avg, max);
  }

}
