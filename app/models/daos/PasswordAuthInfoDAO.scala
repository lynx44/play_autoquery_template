package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import org.example.project.schema.{Account, Query}
import xyz.mattclifton.autoquery.components.UpdateValue
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class PasswordAuthInfoDAO @Inject() (query: Query, passwordHasher: PasswordHasher) extends DelegableAuthInfoDAO[PasswordInfo] {
  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    Future(getAccount(loginInfo).flatMap(toPasswordInfo(_)))
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    getAccount(loginInfo).map(account => {
      query.updateAccounts(
        filter = _.apply(ids = Seq(account.id)),
        values = _.apply(passwordHash = UpdateValue(Option(authInfo.password)), passwordSalt = UpdateValue(authInfo.salt))).execute()
    })

    Future(authInfo)
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = {
    getAccount(loginInfo).map(account => {
      query.updateAccounts(
        filter = _.apply(ids = Seq(account.id)),
        values = _.apply(passwordHash = UpdateValue(None), passwordSalt = UpdateValue(None))).execute()
    })

    Future({})
  }

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    update(loginInfo, authInfo)
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    update(loginInfo, authInfo)
  }

  private def getAccount(loginInfo: LoginInfo): Option[Account] = {
    query.getAccountProviders(
      filter = _.apply(providerIdAndProviderKey = Some(_.apply(Seq(loginInfo.providerID), Seq(loginInfo.providerKey)))),
      include = s => Seq(s.account)).execute().headOption.map(_.account)
  }

  private def toPasswordInfo(account: Account): Option[PasswordInfo] = {
    account.passwordHash.map(hash => PasswordInfo(passwordHasher.id, hash, account.passwordSalt))
  }
}
