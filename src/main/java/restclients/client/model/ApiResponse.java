package restclients.client.model;

import lombok.Value;

/**
 * @author Yuriy Tumakha
 */
@Value
public class ApiResponse {
  int code;
  String body;
}
