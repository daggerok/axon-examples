package daggerok.app.backend;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GiftSummaryProjection {

  final EntityManager em;

  @Transactional
  @ExceptionHandler
  public void on(final IssuedEvt evt) {
    em.persist(new CardSummary(
        evt.getId(),
        evt.getAmount(),
        evt.getAmount()
    ));
  }

  @Transactional
  @ExceptionHandler
  public void on(final RedeemedEvt evt) {
    final CardSummary cs = em.find(CardSummary.class, evt.getId());
    final int balance = Optional.ofNullable(cs.getRemainingBalance()).orElseThrow(NullPointerException::new);
    cs.setRemainingBalance(balance - evt.getAmount());
    em.persist(cs);
  }

  @QueryHandler
  public List<CardSummary> handle(final DataQuery query) {
    return em.createQuery("select cs from CardSummary cs order by cs.id", CardSummary.class)
             .getResultList();
  }

  @QueryHandler
  public Integer handle(final SizeQuery query) {
    return em.createQuery("select count(cs.id) from CardSummary cs", Long.class)
             .getSingleResult()
             .intValue();
  }
}
