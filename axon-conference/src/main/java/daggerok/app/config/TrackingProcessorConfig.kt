package daggerok.app.config

import daggerok.app.Conference
import org.axonframework.config.EventHandlingConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

//tag::content[]
@Configuration
@ComponentScan(basePackageClasses = [
  Conference::class
])
class TrackingProcessorConfig {

  @Autowired
  fun enableTracking(config: EventHandlingConfiguration) {
    config.usingTrackingProcessors()
  }
}
//end::content[]
