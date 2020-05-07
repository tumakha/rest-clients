package restclients.client.impl.apache;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class ApacheHttpClientTest extends RestSpec {

    public ApacheHttpClientTest() {
        client = new ApacheHttpClient();
    }

}
