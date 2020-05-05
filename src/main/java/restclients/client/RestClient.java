package restclients.client;

/**
 * @author Yuriy Tumakha
 */
public interface RestClient {

  String getName();

  ApiResponse sendRequest(ApiRequest request);

}
