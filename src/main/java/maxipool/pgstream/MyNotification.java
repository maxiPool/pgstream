package maxipool.pgstream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record MyNotification(@JsonProperty("action") ESqlAction action,
                             @JsonProperty("new") StreamMeEntity newEntity,
                             @JsonProperty("old") StreamMeEntity oldEntity) {
  @JsonCreator
  public MyNotification {
    // for deserialization
  }
}
