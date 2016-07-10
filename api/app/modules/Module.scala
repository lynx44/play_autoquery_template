package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import org.example.project.rest.models.JsonParsers
import org.example.project.schema.Query

class Module extends AbstractModule with ScalaModule {

  override def configure() = {
    bind(classOf[MigratorInitializer]) asEagerSingleton()
    bind(classOf[CreateSquerylSession]) asEagerSingleton()
    bind(classOf[Query]).toInstance(models.Query)
    bind(classOf[JsonParsers]).toInstance(JsonParsers)
    bind(classOf[CorsFilter]) asEagerSingleton()
  }
}



