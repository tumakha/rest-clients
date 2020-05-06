package restclients.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restclients.benchmark.model.TimeStats;
import restclients.client.impl.asynchttpclient.AsyncHttpRestClient;
import restclients.client.impl.java11.Java11HttpClient;
import restclients.client.impl.spring.SpringWebClient;
import restclients.client.model.ApiRequest;
import restclients.client.RestClient;
import restclients.client.impl.java.SimpleJavaClient;

import java.io.*;
import java.util.List;

import static java.lang.String.format;


/**
 * @author Yuriy Tumakha
 */
@Component
public class PerformanceBenchmark implements CommandLineRunner {

  private static final String REPORT_FILENAME = "rest-clients-performance.csv";

  private final PrintWriter REPORT_WRITER = new PrintWriter(new BufferedWriter(new FileWriter(new File(REPORT_FILENAME))));
  private List<ApiRequest> requests;

  @Autowired
  RequestRunner requestRunner;

  @Autowired
  Scenario scenario;

  public PerformanceBenchmark() throws IOException {
  }

  @Override
  public void run(String... args) throws IOException {
    requests = scenario.getRequests();
    REPORT_WRITER.println("REST Client,Request name,Threads,Requests,Total duration,Time per request,Min,Avg,Max");

    testClient(new SimpleJavaClient());
    testClient(new Java11HttpClient());
    testClient(new AsyncHttpRestClient());
    testClient(new SpringWebClient());

    REPORT_WRITER.close();
  }

  private void testClient(RestClient restClient) {
    try (restClient) {
      System.out.println(restClient.getName());
      requestRunner.setRestClient(restClient);

      for (ApiRequest request : requests) {
        sendRequests(request, 1, 100);
        //sendRequests(request, 2, 100);
        sendRequests(request, 8, 100);
        sendRequestsNonBlocking(request, 8, 100);
      }
      REPORT_WRITER.flush();
    }
  }

  private void sendRequests(ApiRequest request, int threads, int requestsNum) {
    TimeStats stats = requestRunner.run(request, threads, requestsNum);

    REPORT_WRITER.println(format("%s,%s,%d,%d,%d,%d,%.3f,%.3f,%.3f",
            requestRunner.getRestClient().getName(), request.getName(), threads, requestsNum,
            stats.getTotalTime(), stats.getTimePerRequest(), stats.getMin(), stats.getAvg(), stats.getMax()));
  }

  private void sendRequestsNonBlocking(ApiRequest request, int threads, int requestsNum) {
    TimeStats stats = requestRunner.runNonBlocking(request, threads, requestsNum);

    REPORT_WRITER.println(format("%s,%s,%d,%d,%d,%d,%.3f,%.3f,%.3f",
            requestRunner.getRestClient().getName(), request.getName() + " Non-blocking", threads, requestsNum,
            stats.getTotalTime(), stats.getTimePerRequest(), stats.getMin(), stats.getAvg(), stats.getMax()));
  }

}
