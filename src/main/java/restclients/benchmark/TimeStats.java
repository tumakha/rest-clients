package restclients.benchmark;

import lombok.Value;

/**
 * @author Yuriy Tumakha
 */
@Value
public class TimeStats {
  long totalTime;
  double timePerRequest;
  double min;
  double avg;
  double max;
}
