package restclients.client.impl.akka;

import restclients.client.impl.RestSpec;

/**
 * @author Yuriy Tumakha
 */
public class AkkaRestClientTest extends RestSpec {

  public AkkaRestClientTest() {
    client = new AkkaRestClient();
  }
}
