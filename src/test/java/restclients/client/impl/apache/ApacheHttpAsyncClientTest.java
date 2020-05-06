package restclients.client.impl.apache;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class ApacheHttpAsyncClientTest extends RestSpec {

    public ApacheHttpAsyncClientTest() {
        client = new ApacheHttpAsyncClient();
    }

}
