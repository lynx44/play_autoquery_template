
package org.example.project.squeryl.models

import org.squeryl.Schema
import Squeryl._

class DatabaseDefinition extends Schema {
    val account = table[Account]("account")
    val accountProvider = table[AccountProvider]("account_provider")

    val account_account_provider_relation = oneToManyRelation(account, accountProvider).via((l, r) => l.id === r.accountId)

    on(account)(t => declare(t.id is autoIncremented("account_id_seq")))
    on(accountProvider)(t => declare(t.id is autoIncremented("account_provider_id_seq")))
}

object Database extends DatabaseDefinition



