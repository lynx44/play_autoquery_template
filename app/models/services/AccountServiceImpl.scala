package models.services

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.User
import org.example.project.schema.{AccountProvider, Query}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

class AccountServiceImpl @Inject() (query: Query) extends UserService {
  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User): Future[User] = {
    val insertedAccountResult = query.insertAccount(_.apply(user.firstName, user.lastName, user.email, user.avatarURL)).execute()
    insertedAccountResult.entity match {
      case Left(account) => {
        query.insertAccountProvider(_.apply(account.id, user.loginInfo.providerID, user.loginInfo.providerKey)).execute()
        Future(user.copy(id = account.id))
      }
      case Right(e) => throw e
    }
  }

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User] = ???

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    Future(query.getAccountProviders(
      filter = _.apply(providerIdAndProviderKey = Some(_.apply(Seq(loginInfo.providerID), Seq(loginInfo.providerKey)))),
      include = s => Seq(s.account)).execute().headOption.map(toUser(_)))
  }

  private def toUser(accountProvider: AccountProvider): User = {
    val firstAndLastSeq = accountProvider.account.firstName.toSeq ++ accountProvider.account.lastName.toSeq
    User(
      accountProvider.account.id,
      LoginInfo(accountProvider.providerId, accountProvider.providerKey),
      accountProvider.account.firstName,
      accountProvider.account.lastName,
      if(firstAndLastSeq.nonEmpty) Some(firstAndLastSeq.mkString(" ")) else None,
      accountProvider.account.email,
      accountProvider.account.avatarURL)
  }
}
