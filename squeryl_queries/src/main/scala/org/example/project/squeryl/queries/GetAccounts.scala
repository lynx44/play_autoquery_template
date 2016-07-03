
package org.example.project.squeryl.queries

import org.example.project.squeryl.models.Squeryl._
import org.example.project.squeryl.models.Database._

class GetAccounts(filter: org.example.project.schema.GetAccountsFilter) extends xyz.mattclifton.autoquery.components.Query[scala.Seq[org.example.project.schema.Account]] {
    private implicit def db = org.example.project.squeryl.models.Database
    def execute(): scala.Seq[org.example.project.schema.Account] = {
        inTransaction {
            from(account)(d =>
                where(((d.email in filter.emails).inhibitWhen(filter.emails.isEmpty)))
                select(d)
                
            ).toList
        }
    }
}