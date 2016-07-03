package org.example.project.schema

import xyz.mattclifton.autoquery.components._

trait Query {
  def getAccounts(filter: GetAccountsFilter.type => GetAccountsFilter): xyz.mattclifton.autoquery.components.Query[Seq[Account]]
  def getAccountProviders(filter: GetAccountProviderFilter.type => GetAccountProviderFilter = _.apply(), include: AccountProviderSelector => Seq[Selector] = q => Seq(NullSelector)): xyz.mattclifton.autoquery.components.Query[Seq[AccountProvider]]
  def updateAccounts(filter: UpdateAccountsFilter.type => UpdateAccountsFilter, values: UpdateAccountsValues.type => UpdateAccountsValues): xyz.mattclifton.autoquery.components.Query[UpdateResult[Account]]
//  def deleteAccounts(filter: GetAccountFilter.type => GetAccountFilter): xyz.mattclifton.autoquery.components.Query[UpdateResult[Account]]
  def insertAccount(values: InsertAccount.type => InsertAccount): xyz.mattclifton.autoquery.components.Query[InsertResult[Account]]
  def insertAccountProvider(values: InsertAccountProvider.type => InsertAccountProvider): xyz.mattclifton.autoquery.components.Query[InsertResult[AccountProvider]]
}

case class GetAccountsFilter(emails: Seq[String] = Seq())

case class UpdateAccountsFilter(ids: Seq[Int] = Seq())
case class UpdateAccountsValues(
                                 firstName: UpdateValue[Option[String]] = UpdateValue(),
                                 lastName: UpdateValue[Option[String]] = UpdateValue(),
                                 avatarURL: UpdateValue[Option[String]] = UpdateValue(),
                                 passwordHash: UpdateValue[Option[String]] = UpdateValue(),
                                 passwordSalt: UpdateValue[Option[String]] = UpdateValue())

case class GetAccountProviderFilter(providerIdAndProviderKey: Option[ProviderIdAndProviderKey.type => ProviderIdAndProviderKey] = None)
case class ProviderIdAndProviderKey(providerId: Seq[String], providerKey: Seq[String])

case class InsertAccount(firstName: Option[String],
                          lastName: Option[String],
                          email: Option[String],
                          avatarURL: Option[String],
                          passwordHash: Option[String] = None,
                          passwordSalt: Option[String] = None)

case class InsertAccountProvider(
                                accountId: Int,
                                providerId: String,
                                providerKey: String)

case class AccountProviderSelector(parent: Option[Selector]) extends Selector {
  def account = new AccountSelector(Some(this))
}
case class AccountSelector(parent: Option[Selector]) extends Selector {
}