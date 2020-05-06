package restclients.client.impl;

import org.junit.jupiter.api.Test;
import restclients.client.model.ApiResponse;
import restclients.client.RestClient;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsStringIgnoringCase;

/**
 * @author Yuriy Tumakha
 */
public abstract class RestSpec {

    private static final Map<String, String> HEADERS = Map.of(
            "Accept", "application/json",
            "api-key", "ABC12345");

    protected RestClient client;

    @Test
    public void testGet() throws IOException {
        String url = "https://postman-echo.com/get?foo1=bar1&foo2=bar2";
        ApiResponse response = client.get(new URL(url), HEADERS);

        assertThat(response.getCode(), equalTo(HTTP_OK));
        assertThat(response.getBody(), allOf(containsStringIgnoringCase("\"foo2\":\"bar2\""),
                containsStringIgnoringCase("\"Accept\":\"application/json\"")));
    }

    @Test
    public void testPost() throws IOException {
        String url = "https://postman-echo.com/post?param1=value1&param2=value2";
        String requestBody = "raw body";
        ApiResponse response = client.post(new URL(url), HEADERS, requestBody);

        assertThat(response.getCode(), equalTo(HTTP_OK));
        assertThat(response.getBody(), allOf(containsStringIgnoringCase("\"param1\":\"value1\""),
                containsStringIgnoringCase("\"api-key\":\"ABC12345\""),
                containsStringIgnoringCase(requestBody)));
    }

    @Test
    public void testDelete() throws IOException {
        String url = "https://postman-echo.com/delete?force=true";
        String requestBody = "delete request body";
        ApiResponse response = client.delete(new URL(url), HEADERS, requestBody);

        assertThat(response.getCode(), equalTo(HTTP_OK));
        assertThat(response.getBody(), allOf(containsStringIgnoringCase("\"force\":\"true\""),
                containsStringIgnoringCase("\"Accept\":\"application/json\""),
                containsStringIgnoringCase(requestBody)));
    }

}
