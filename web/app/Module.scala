import com.google.inject.AbstractModule
import java.time.Clock
import org.example.project.rest.models.JsonParsers
import services.{ApplicationTimer, AtomicCounter, Counter}

import scala.concurrent.ExecutionContext

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])
    bind(classOf[JsonParsers]).toInstance(JsonParsers)
    bind(classOf[ExecutionContext]).toInstance(play.api.libs.concurrent.Execution.Implicits.defaultContext)
  }

}
