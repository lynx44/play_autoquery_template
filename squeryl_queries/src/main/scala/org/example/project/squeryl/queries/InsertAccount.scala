
package org.example.project.squeryl.queries

import org.example.project.squeryl.models.Squeryl._
import org.example.project.squeryl.models.Database._

class InsertAccount(values: org.example.project.schema.InsertAccount) extends xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.Account]] {
  def execute(): xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.Account] = {
    val result = scala.util.Try(
        inTransaction {
            account.insert(org.example.project.squeryl.models.Account(id = 0, passwordSalt = values.passwordSalt, passwordHash = values.passwordHash, avatarURL = values.avatarURL, email = values.email, lastName = values.lastName, firstName = values.firstName))
        })

    new xyz.mattclifton.autoquery.components.InsertResultImpl[org.example.project.schema.Account](
        result match {
          case scala.util.Success(s) => Left(s)
          case scala.util.Failure(e) => Right(e)
        }
    )
  }
}