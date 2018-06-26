package daggerok.app.backend;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor(access = PRIVATE)
public class GiftCard {

  @AggregateIdentifier
  private String id;
  private int balance;

  @CommandHandler
  public GiftCard(final IssueCmd cmd) {

    final String message = format("amount (%d) <= 0", cmd.getAmount());
    if (cmd.getAmount() <= 0) throw new IllegalArgumentException(message);

    apply(new IssuedEvt(cmd.getId(), cmd.getAmount()));
  }

  @CommandHandler
  public void handle(final RedeemCmd cmd) {

    String message = format("amount (%d) <= 0", cmd.getAmount());
    if (cmd.getAmount() <= 0) throw new IllegalArgumentException(message);

    message = format("amount (%d) > balance (%d)", cmd.getAmount(), balance);
    if (cmd.getAmount() > balance) throw new IllegalArgumentException(message);

    apply(new RedeemedEvt(cmd.getId(), cmd.getAmount()));
  }

  @EventSourcingHandler
  public void on(final IssuedEvt evt) {
    id = evt.getId();
    balance = evt.getAmount();
  }

  @EventSourcingHandler
  public void on(final RedeemedEvt evt) {
    balance -= evt.getAmount();
  }
}
