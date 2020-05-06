package restclients.client.impl.restclients.client.impl.spring;

import restclients.client.impl.RestSpec;
import restclients.client.impl.spring.SpringWebClient;

/**
 * @author Yuriy Tumakha
 */
public class SpringWebClientTest extends RestSpec {

    public SpringWebClientTest() {
        client = new SpringWebClient();
    }

}
