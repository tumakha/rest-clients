package restclients.client;

import lombok.Value;

import java.net.URL;

/**
 * @author Yuriy Tumakha
 */
@Value
public class ApiRequest {

  String name;

  URL url;

}
