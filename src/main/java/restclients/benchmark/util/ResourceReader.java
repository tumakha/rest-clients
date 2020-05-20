package restclients.benchmark.util;

import org.springframework.core.io.Resource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.StreamUtils.copyToString;

/**
 * @author Yuriy Tumakha
 */
public interface ResourceReader {

  default String asString(Resource resource) throws IOException {
    return copyToString(resource.getInputStream(), UTF_8);
  }

}
