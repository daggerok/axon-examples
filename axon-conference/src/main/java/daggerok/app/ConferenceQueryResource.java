package daggerok.app;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.management.ServiceNotFoundException;
import javax.validation.Valid;
import java.util.List;

import static daggerok.app.Requsts.CreateRequest;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class ConferenceQueryResource {

  final SpeakerQueryModelRepository speakers;
  final ConferenceQueryModelRepository conferences;

  @GetMapping("/speaker")
  public List<SpeakerQueryModel> speakers() {
    return speakers.findAll();
  }

  @GetMapping("/speaker/{id}")
  public SpeakerQueryModel speakers(@PathVariable final String id) {
    return speakers.findById(id).orElseThrow(SpeakerNotFoundException::new);
  }

  @GetMapping("/conference")
  public List<ConferenceQueryModel> conferences() {
    return conferences.findAll();
  }
}
