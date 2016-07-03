
package org.example.project.squeryl.queries

import org.example.project.squeryl.models.Squeryl._
import org.example.project.squeryl.models.Database._

class UpdateAccounts(filter: org.example.project.schema.UpdateAccountsFilter, values: org.example.project.schema.UpdateAccountsValues) extends xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.UpdateResult[org.example.project.schema.Account]] {
  private implicit def db = org.example.project.squeryl.models.Database
  def execute(): xyz.mattclifton.autoquery.components.UpdateResult[org.example.project.schema.Account] = {
    inTransaction {
      if((values.passwordSalt.nonEmpty) || (values.passwordHash.nonEmpty) || (values.avatarURL.nonEmpty) || (values.lastName.nonEmpty) || (values.firstName.nonEmpty)) {
          update(account)(d =>
          where(((d.id in filter.ids).inhibitWhen(filter.ids.isEmpty)))
          set(dynamic(
            (d.passwordSalt := values.passwordSalt.getOrElse(scala.None.asInstanceOf[scala.Option[scala.Predef.String]])).inhibitWhen(values.passwordSalt.isEmpty), 
(d.passwordHash := values.passwordHash.getOrElse(scala.None.asInstanceOf[scala.Option[scala.Predef.String]])).inhibitWhen(values.passwordHash.isEmpty), 
(d.avatarURL := values.avatarURL.getOrElse(scala.None.asInstanceOf[scala.Option[scala.Predef.String]])).inhibitWhen(values.avatarURL.isEmpty), 
(d.lastName := values.lastName.getOrElse(scala.None.asInstanceOf[scala.Option[scala.Predef.String]])).inhibitWhen(values.lastName.isEmpty), 
(d.firstName := values.firstName.getOrElse(scala.None.asInstanceOf[scala.Option[scala.Predef.String]])).inhibitWhen(values.firstName.isEmpty)
          ):_*))
      }
    }

    new xyz.mattclifton.autoquery.components.UpdateResultImpl[org.example.project.schema.Account]
  }
}