package restclients.client.impl.okhttp;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class OkHttpRestClientTest extends RestSpec {

  public OkHttpRestClientTest() {
    client = new OkHttpRestClient();
  }

}
