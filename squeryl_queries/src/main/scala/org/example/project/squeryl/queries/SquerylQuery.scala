
package org.example.project.squeryl.queries

trait SquerylQuery extends org.example.project.schema.Query {
    
    def getAccountProviders(filters: (org.example.project.schema.GetAccountProviderFilter.type) => org.example.project.schema.GetAccountProviderFilter, includes: (org.example.project.schema.AccountProviderSelector) => Seq[xyz.mattclifton.autoquery.components.Selector]): xyz.mattclifton.autoquery.components.Query[scala.Seq[org.example.project.schema.AccountProvider]] = {
        new org.example.project.squeryl.queries.GetAccountProviders(filters.apply(org.example.project.schema.GetAccountProviderFilter), new xyz.mattclifton.autoquery.components.SelectedPath(includes(new org.example.project.schema.AccountProviderSelector(None)), new org.example.project.schema.AccountProviderSelector(None)))
    }

    def getAccounts(filters: (org.example.project.schema.GetAccountsFilter.type) => org.example.project.schema.GetAccountsFilter): xyz.mattclifton.autoquery.components.Query[scala.Seq[org.example.project.schema.Account]] = {
        new org.example.project.squeryl.queries.GetAccounts(filters.apply(org.example.project.schema.GetAccountsFilter))
    }

    
    def updateAccounts(filters: (org.example.project.schema.UpdateAccountsFilter.type) => org.example.project.schema.UpdateAccountsFilter, values: (org.example.project.schema.UpdateAccountsValues.type) => org.example.project.schema.UpdateAccountsValues): xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.UpdateResult[org.example.project.schema.Account]] = {
        new org.example.project.squeryl.queries.UpdateAccounts(filters.apply(org.example.project.schema.UpdateAccountsFilter), values.apply(org.example.project.schema.UpdateAccountsValues))
    }

    
    def insertAccountProvider(values: (org.example.project.schema.InsertAccountProvider.type) => org.example.project.schema.InsertAccountProvider): xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.AccountProvider]] = {
        new org.example.project.squeryl.queries.InsertAccountProvider(values.apply(org.example.project.schema.InsertAccountProvider))
    }

    def insertAccount(values: (org.example.project.schema.InsertAccount.type) => org.example.project.schema.InsertAccount): xyz.mattclifton.autoquery.components.Query[xyz.mattclifton.autoquery.components.InsertResult[org.example.project.schema.Account]] = {
        new org.example.project.squeryl.queries.InsertAccount(values.apply(org.example.project.schema.InsertAccount))
    }

    
}







