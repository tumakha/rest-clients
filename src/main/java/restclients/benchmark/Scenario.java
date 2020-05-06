package restclients.benchmark;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import restclients.client.model.ApiRequest;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static restclients.client.model.HttpMethod.*;

/**
 * @author Yuriy Tumakha
 */
@Component
public class Scenario implements ResourceReader {

    @Value("classpath:POST.json")
    private Resource postBody;

    public List<ApiRequest> getRequests() throws IOException {
        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Accept", "application/json"
        );

        return List.of(
                //new ApiRequest(GET, new URL("https://postman-echo.com/get?foo1=bar1&foo2=bar2"), headers, null),
                new ApiRequest(POST, new URL("https://postman-echo.com/post"), headers, asString(postBody)),
                new ApiRequest(DELETE, new URL("https://www.upwork.com/api"), null, "{\"force\": \"true\"}")
        );
    }

}
