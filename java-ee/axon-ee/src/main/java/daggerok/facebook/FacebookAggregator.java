package daggerok.facebook;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@NoArgsConstructor
public class FacebookAggregator {

  @AggregateIdentifier
  String postId;
  long likes, dislikes;

  /* create */
  @CommandHandler
  public FacebookAggregator(final CreatePostCommand cmd) {
    apply(new PostCreatedEvent(cmd.getPostId()));
  }

  @EventSourcingHandler
  public void on(final PostCreatedEvent event) {
    postId = event.getPostId();
    likes = dislikes = 0;
  }

  /* add */
  @CommandHandler
  public void handle(final LikePostCommand cmd) {
    apply(new PostLikedEvent(cmd.getPostId()));
  }

  @EventSourcingHandler
  public void on(final PostLikedEvent event) {
    this.likes++;
  }

  /* subtract */
  @CommandHandler
  public void handle(final DislikePostCommand cmd) {
    apply(new PostDislikedEvent(cmd.getPostId()));
  }

  @EventSourcingHandler
  public void on(final PostDislikedEvent event) {
    this.dislikes++;
  }
}
