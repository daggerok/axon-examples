package daggerok.facebook;

import daggerok.facebook.axon.Axon;
import daggerok.facebook.repository.PostQueryModel;
import daggerok.facebook.repository.PostQueryModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.DomainEventMessage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Path("posts")
@ApplicationScoped
@Produces(APPLICATION_JSON)
public class FacebookPostsQueryProjectionResource {

  @Inject @Axon Configuration axon;
  @Inject PostQueryModelRepository repository;

/* TODO: Fuck.. Spring but not JavaEE?? /=
  @EventHandler
  public void on(final PostCreatedEvent event, @Timestamp Instant createdAt) {
      log.info("handling event: {}", event);
      repository.save(new PostQueryModel().setPostId(event.getPostId())
                                        .setDislikes(0L).setLikes(0L).setCreatedAt(createdAt));
  }
*/

  @PostConstruct
  public void initialization() {

    final BiConsumer<Class<?>, Consumer<EventMessage<?>>> listener = (c, handler) ->
        axon.eventBus().subscribe(events -> events
            .stream()
            .filter(e -> c.isAssignableFrom(e.getPayloadType()))
            .forEachOrdered(handler));

    listener.accept(PostCreatedEvent.class, this::handlePostCreatedEvent);
    listener.accept(PostLikedEvent.class, this::handlePostLikedEvent);
    listener.accept(PostDislikedEvent.class, this::handlePostDislikedEvent);
  }

  void handlePostCreatedEvent(final EventMessage<?> eventMessage) {

    final PostCreatedEvent event = PostCreatedEvent.class.cast(eventMessage.getPayload());

    repository.save(new PostQueryModel().setPostId(event.getPostId())
                                        .setDislikes(0L)
                                        .setLikes(0L)
                                        .setCreatedAt(eventMessage.getTimestamp()));
  }

  void handlePostLikedEvent(final EventMessage<?> eventMessage) {

    final PostLikedEvent event = PostLikedEvent.class.cast(eventMessage.getPayload());

    ofNullable(repository.findFirstLike(event.getPostId()))
        .ifPresent(postQueryModel -> repository.save(
            postQueryModel.setModifiedAt(eventMessage.getTimestamp())
                          .setLikes(postQueryModel.getLikes() + 1)));
  }

  void handlePostDislikedEvent(final EventMessage<?> eventMessage) {

    final PostDislikedEvent event = PostDislikedEvent.class.cast(eventMessage.getPayload());

    ofNullable(repository.findFirstLike(event.getPostId()))
        .ifPresent(postQueryModel -> repository.save(
            postQueryModel.setModifiedAt(eventMessage.getTimestamp())
                          .setDislikes(postQueryModel.getLikes() + 1)));
  }

  public void initialized(@Observes @Initialized(ApplicationScoped.class) Object init) {
    log.info("axon event listener configured");
  }

  public void destroyed(@Observes @Destroyed(ApplicationScoped.class) Object init) {
    log.info("clear");
  }

  @GET
  @Path("{postId}/events")
  public List<? extends DomainEventMessage<?>> getEvents(@PathParam("postId") final String postId) {
    return axon.eventStore()
               .readEvents(postId)
               .asStream()
               .collect(toList());
  }

  @GET
  @Path("{id}")
  public PostQueryModel getOne(@PathParam("id") final String id) {
    return repository.findOne(id);
  }

  @GET
  @Path("")
  public List<PostQueryModel> getAll() {
    return repository.findAll();
  }
}
