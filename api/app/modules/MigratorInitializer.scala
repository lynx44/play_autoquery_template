package modules

import javax.inject.Inject

import play.api.db.DBApi
import xyz.mattclifton.autoschema.{DatabaseBuilder, Migrator}

import scala.reflect.runtime.universe._


class MigratorInitializer @Inject()(dBApi: DBApi, environment: play.api.Environment) {

  new Migrator(dBApi.databases().head.dataSource, dBApi.databases().head.name)
    .run(new DatabaseBuilder(Some(environment.classLoader)).build(
      Seq(
        typeOf[org.example.project.schema.Account],
        typeOf[org.example.project.schema.AccountProvider]
      )
    ).database)
}

