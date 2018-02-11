package daggerok.facebook.axon;

import daggerok.facebook.FacebookAggregator;
import daggerok.facebook.FacebookPostsQueryProjectionResource;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@Slf4j
@ApplicationScoped
public class AxonFactory {

  Configuration configuration;

  @PostConstruct
  public void configuration() {
    log.info("axon initialization...");
    configuration = DefaultConfigurer.defaultConfiguration()
                                     .configureAggregate(FacebookPostsQueryProjectionResource.class)
                                     .configureAggregate(FacebookAggregator.class)
                                     .configureEmbeddedEventStore(c -> new InMemoryEventStorageEngine())
                                     .buildConfiguration();
    configuration.start();
  }

  @Axon
  @Produces
  public Configuration axon() {
    return configuration;
  }
}
