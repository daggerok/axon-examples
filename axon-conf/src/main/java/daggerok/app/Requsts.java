package daggerok.app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public abstract class Requsts {

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CreateRequest {
    @NotBlank(message = "Conference name is required.")
    String name;
  }

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AddTalkRequest {
    @NotBlank(message = "Talk name is required.")
    String talk;
    @NotBlank(message = "Talk speaker is required.")
    String speaker;
  }
}
