package restclients.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import restclients.client.ApiRequest;
import restclients.client.RestClient;
import restclients.client.java.SimpleJavaClient;

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
  private final List<ApiRequest> requests;

  @Autowired
  RequestRunner requestRunner;

  public PerformanceBenchmark() throws IOException {
    requests = List.of(null);
  }

  @Override
  public void run(String... args) {
    REPORT_WRITER.println("REST Client,Request name,Threads,Requests,Total duration,Time per request,Min,Avg,Max");

    testClient(new SimpleJavaClient());
    //testClient(new SimpleJavaClient());

    REPORT_WRITER.close();
  }

  private void testClient(RestClient restClient) {
    requestRunner.setRestClient(restClient);

    for (ApiRequest request : requests) {
      sendRequests(request, 1, 1_000);
      sendRequests(request, 2, 1_000);
      sendRequests(request, 4, 1_000);
    }

    REPORT_WRITER.flush();
  }

  private void sendRequests(ApiRequest request, int threads, int requestsNum) {
    TimeStats stats = requestRunner.run(request, threads, requestsNum);

    REPORT_WRITER.println(format("%s,%s,%d,%d,%d,%.3f,%.3f,%.3f,%.3f",
        requestRunner.getRestClient().getName(), request.getName(), threads, requestsNum,
        stats.getTotalTime(), stats.getTimePerRequest(), stats.getMin(), stats.getAvg(), stats.getMax()));
  }

}
