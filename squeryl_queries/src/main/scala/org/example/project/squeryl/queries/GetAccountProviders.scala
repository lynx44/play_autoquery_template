
package org.example.project.squeryl.queries

import org.example.project.squeryl.models.Squeryl._
import org.example.project.squeryl.models.Database._

class GetAccountProviders(filter: org.example.project.schema.GetAccountProviderFilter, includes: xyz.mattclifton.autoquery.components.SelectedPath[org.example.project.schema.AccountProviderSelector]) extends xyz.mattclifton.autoquery.components.Query[scala.Seq[org.example.project.schema.AccountProvider]] {
    private implicit def db = org.example.project.squeryl.models.Database
    def execute(): scala.Seq[org.example.project.schema.AccountProvider] = {
        inTransaction {
            from(accountProvider)(d =>
                where((((((d.providerKey in filter.providerIdAndProviderKey.map(_.apply(org.example.project.schema.ProviderIdAndProviderKey).providerKey).getOrElse(Seq()))) and ((d.providerId in filter.providerIdAndProviderKey.map(_.apply(org.example.project.schema.ProviderIdAndProviderKey).providerId).getOrElse(Seq())))).inhibitWhen(filter.providerIdAndProviderKey.map(_.apply(org.example.project.schema.ProviderIdAndProviderKey)).isEmpty))))
                select(d)
                include(_->>(_.*-(_.accountRelation).inhibitWhen(!includes.includesPath(_.account))))
            ).toList
        }
    }
}