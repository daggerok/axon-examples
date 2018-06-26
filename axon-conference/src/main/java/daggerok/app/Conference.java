package daggerok.app;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.*;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor(access = PRIVATE)
public class Conference {

  @AggregateIdentifier
  private String id;
  private String name;
  private List<String> talks;

  @CommandHandler
  public Conference(final CreateConferenceCommand cmd) {
    apply(new ConferenceCreatedEvent(cmd.getId(), cmd.getName()));
  }

  @EventSourcingHandler
  public void on(final ConferenceCreatedEvent e) {
    this.id = e.getId();
    this.name = e.getName();
    this.talks = new ArrayList<>();
  }

  @CommandHandler
  public void handle(final AddTalkCommand cmd) {
    final String talk = cmd.getTalk();
    if (talks.contains(talk)) throw new DuplicatedTaskException("talks already contains talk " + talk);
    apply(new TalkAddedEvent(cmd.getId(), talk, cmd.getSpeaker()));
  }

  @EventSourcingHandler
  public void on(final TalkAddedEvent e) {
    this.talks.add(e.getTalk());
  }
}
