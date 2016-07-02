package org.example.project.schema

import xyz.mattclifton.autoquery.components.{InsertResult, Selector, NullSelector}

trait Query {
  def getAccountProviders(filter: GetAccountProviderFilter.type => GetAccountProviderFilter = _.apply(), include: AccountProviderSelector => Seq[Selector] = q => Seq(NullSelector)): xyz.mattclifton.autoquery.components.Query[Seq[AccountProvider]]
  def insertAccount(values: InsertAccount.type => InsertAccount): xyz.mattclifton.autoquery.components.Query[InsertResult[Account]]
  def insertAccountProvider(values: InsertAccountProvider.type => InsertAccountProvider): xyz.mattclifton.autoquery.components.Query[InsertResult[AccountProvider]]
}

case class GetAccountProviderFilter(providerIdAndProviderKey: Option[ProviderIdAndProviderKey.type => ProviderIdAndProviderKey] = None)
case class ProviderIdAndProviderKey(providerId: Seq[String], providerKey: Seq[String])

case class InsertAccount(firstName: Option[String],
lastName: Option[String],
email: Option[String],
avatarURL: Option[String])

case class InsertAccountProvider(
accountId: Int,
providerId: String,
providerKey: String)

case class AccountProviderSelector(parent: Option[Selector]) extends Selector {
  def account = new AccountSelector(Some(this))
}
case class AccountSelector(parent: Option[Selector]) extends Selector {
}