package restclients.benchmark.model;

import lombok.Value;

/**
 * @author Yuriy Tumakha
 */
@Value
public class TimeStats {
  long totalTime;
  long timePerRequest;
  double min;
  double avg;
  double max;
}
