
package org.example.project.squeryl.queries

import org.example.project.squeryl.models.Squeryl._
import org.example.project.squeryl.models.Database._

class InsertAccountProvider(values: org.example.project.schema.InsertAccountProvider) extends xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.AccountProvider]] {
  def execute(): xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.AccountProvider] = {
    val result = scala.util.Try(
        inTransaction {
            accountProvider.insert(org.example.project.squeryl.models.AccountProvider(id = 0, providerKey = values.providerKey, providerId = values.providerId, accountId = values.accountId))
        })

    new xyz.mattclifton.autoquery.components.InsertResultImpl[org.example.project.schema.AccountProvider](
        result match {
          case scala.util.Success(s) => Left(s)
          case scala.util.Failure(e) => Right(e)
        }
    )
  }
}