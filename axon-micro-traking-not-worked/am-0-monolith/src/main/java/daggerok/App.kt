package daggerok

import org.axonframework.config.EventHandlingConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class App(val eventHandlingConfiguration: EventHandlingConfiguration) {

  @Autowired
  fun configTrackingEventProcessors() {
    eventHandlingConfiguration.usingTrackingProcessors()
  }
}

fun main(args: Array<String>) {
  runApplication<App>(*args)
}
