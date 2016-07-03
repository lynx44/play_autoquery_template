
package org.example.project.squeryl.models

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class AccountProvider(
    @Column("id") id: scala.Int, 
    @Column("provider_id") providerId: scala.Predef.String, 
    @Column("provider_key") providerKey: scala.Predef.String, 
    @Column("account_id") accountId: Int)
    extends KeyedEntity[scala.Int]
    with org.example.project.schema.AccountProvider {
    lazy val accountRelation = Database.account_account_provider_relation.rightIncludable(this)
    lazy val account: org.example.project.schema.Account = accountRelation.one.get
}


