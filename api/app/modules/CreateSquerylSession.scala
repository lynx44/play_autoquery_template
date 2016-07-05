package modules

import javax.inject.Inject

import org.squeryl.adapters.{H2Adapter, PostgreSqlAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import play.api.db.DBApi


class CreateSquerylSession @Inject()(configuration: play.api.Configuration, dBApi: DBApi) {
  SessionFactory.concreteFactory = configuration.getString("db.default.driver") match {
    case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter))
    case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter()))
    case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver")
  }

  def getSession(adapter:DatabaseAdapter) = Session.create(dBApi.databases().head.getConnection(), adapter)
}