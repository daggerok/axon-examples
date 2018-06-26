package daggerok.app;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

import static daggerok.app.Requsts.*;
import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class ConferenceCommandResource {

  final CommandGateway commandGateway;

  @PostMapping("/conf")
  public Map create(@Valid @RequestBody final CreateRequest req) {
    final String id = UUID.randomUUID().toString();
    commandGateway.send(new CreateConferenceCommand(id, req.getName()), LoggingCallback.INSTANCE);
    return singletonMap("id", id);
  }

  @ResponseStatus(ACCEPTED)
  @PostMapping("/conf/{id}")
  public void create(@PathVariable final String id, @Valid @RequestBody final AddTalkRequest req) {
    commandGateway.send(new AddTalkCommand(id, req.getTalk(), req.getSpeaker()), LoggingCallback.INSTANCE);
  }
}
