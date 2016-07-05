package modules

import com.google.inject.AbstractModule
import org.example.project.schema.Query

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[MigratorInitializer]) asEagerSingleton()
    bind(classOf[CreateSquerylSession]) asEagerSingleton()
    bind(classOf[Query]).toInstance(models.Query)
  }
}



