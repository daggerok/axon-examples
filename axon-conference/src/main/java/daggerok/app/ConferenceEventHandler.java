package daggerok.app;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConferenceEventHandler {

  final SpeakerQueryModelRepository speakers;
  final ConferenceQueryModelRepository conferences;

  @EventHandler
  @Transactional
  public void on(final ConferenceCreatedEvent event) {
    conferences.save(new ConferenceQueryModel(
        event.getId(),
        event.getName()
    ));
  }

  @EventHandler
  @Transactional
  public void on(final TalkAddedEvent event) {
    final SpeakerQueryModel speaker = speakers.findById(event.getSpeaker())
                                              .orElse(new SpeakerQueryModel(event.getSpeaker(), new ArrayList<>()));
    speaker.getTalks().add(event.getTalk());
    speakers.save(speaker);
  }
}
