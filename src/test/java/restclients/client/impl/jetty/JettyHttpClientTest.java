package restclients.client.impl.jetty;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class JettyHttpClientTest extends RestSpec {

  public JettyHttpClientTest() throws Exception {
    client = new JettyHttpClient();
  }

}
