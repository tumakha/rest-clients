package restclients.client.impl.java11;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class Java11HttpClientTest extends RestSpec {

    public Java11HttpClientTest() {
        client = new Java11HttpClient();
    }

}
