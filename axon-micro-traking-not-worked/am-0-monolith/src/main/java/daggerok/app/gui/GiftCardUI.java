package daggerok.app.gui;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import daggerok.app.backend.*;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;
import static com.vaadin.ui.Notification.Type.HUMANIZED_MESSAGE;
import static com.vaadin.ui.Notification.show;

@Slf4j
@SpringUI
@RequiredArgsConstructor
public class GiftCardUI extends UI {

  final CommandGateway cg;
  final QueryGateway qg;

  DataProvider<CardSummary, Void> dataProvider;

  @PostConstruct
  public void createDataProvider() {
    dataProvider = new AbstractBackEndDataProvider<CardSummary, Void>() {
      @Override
      protected Stream<CardSummary> fetchFromBackEnd(Query<CardSummary, Void> query) {
        return qg.query(new DataQuery(query.getOffset(), query.getLimit()),
                        ResponseTypes.multipleInstancesOf(CardSummary.class))
                 .join()
                 .stream();

      }

      @Override
      protected int sizeInBackEnd(Query<CardSummary, Void> query) {
        return qg.query(new SizeQuery(), ResponseTypes.multipleInstancesOf(Integer.class))
                 .join()
                 .size();
      }
    };
  }

  @Override
  protected void init(VaadinRequest vaadinRequest) {

    final HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.addComponents(issuePanel(), redeemPanel());
    horizontalLayout.setSizeFull();

    final VerticalLayout verticalLayout = new VerticalLayout();
    verticalLayout.addComponents(horizontalLayout, summaryGrid());
    verticalLayout.setSizeFull();

    setContent(verticalLayout);
    //setSizeFull();

    final Consumer<Throwable> handle = e -> {
      Throwable curr = e;
      while (curr != null && !curr.getClass().isAssignableFrom(IllegalArgumentException.class))
        curr = curr.getCause();

      Optional
          .ofNullable(curr).ifPresent(c -> Optional
          .ofNullable(c.getMessage()).ifPresent(message -> show(message, ERROR_MESSAGE)));
    };

    setErrorHandler(event -> {
      final Throwable e = event.getThrowable();
      log.error("{}", e.getLocalizedMessage(), e);
      handle.accept(e);
      handle.accept(e.getCause());
    });
  }

  private Grid<CardSummary> summaryGrid() {

    final Grid<CardSummary> grid = new Grid<>();

    grid.addColumn(CardSummary::getId).setCaption("Id");
    grid.addColumn(CardSummary::getInitialBalance).setCaption("Initial Balance");
    grid.addColumn(CardSummary::getRemainingBalance).setCaption("Remaining Balance");

    grid.setDataProvider(dataProvider);

    grid.setSizeFull();
    return grid;
  }

  private Panel issuePanel() {

    final TextField id = new TextField("Id");
    final TextField amount = new TextField("Amount");
    final Button submit = new Button("Submit");

    submit.addClickListener(clickEvent -> {

      final int parsedAmount = Integer.parseInt(amount.getValue());
      final IssueCmd cmd = new IssueCmd(id.getValue(), parsedAmount);

      //gw.sendAndWait(cmd);
      //show("Success", HUMANIZED_MESSAGE);
      cg.send(cmd, new CommandCallback<IssueCmd, Object>() {
        @Override
        public void onSuccess(CommandMessage<? extends IssueCmd> commandMessage, Object result) {
          show("Success", HUMANIZED_MESSAGE)
              .addCloseListener(e -> dataProvider.refreshAll());
        }

        @Override
        public void onFailure(CommandMessage<? extends IssueCmd> commandMessage, Throwable cause) {
          show(cause.getLocalizedMessage(), ERROR_MESSAGE);
        }
      });
    });

    final FormLayout form = new FormLayout();
    form.setMargin(true);
    form.addComponents(id, amount, submit);

    final Panel panel = new Panel();
    panel.setContent(form);
    return panel;
  }

  private Panel redeemPanel() {

    final TextField id = new TextField("Id");
    final TextField amount = new TextField("Amount");
    final Button submit = new Button("Submit");

    submit.addClickListener(clickEvent -> {

      final int parsedAmount = Integer.parseInt(amount.getValue());
      final RedeemCmd cmd = new RedeemCmd(id.getValue(), parsedAmount);

      //gw.sendAndWait(cmd);
      //show("Success", HUMANIZED_MESSAGE);
      Try.run(() -> cg.sendAndWait(cmd))
         .onFailure(throwable -> show(throwable.getLocalizedMessage(), ERROR_MESSAGE))
         .onSuccess(aVoid -> show("Success", HUMANIZED_MESSAGE)
             .addCloseListener(e -> dataProvider.refreshAll()));
    });

    final FormLayout form = new FormLayout();
    form.setMargin(true);
    form.addComponents(id, amount, submit);

    final Panel panel = new Panel();
    panel.setContent(form);
    return panel;
  }
}
