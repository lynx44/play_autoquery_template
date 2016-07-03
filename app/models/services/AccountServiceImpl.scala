package models.services

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.User
import org.example.project.schema.{Account, AccountProvider, Query}
import play.api.libs.concurrent.Execution.Implicits._
import xyz.mattclifton.autoquery.components.UpdateValue

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
  def save(profile: CommonSocialProfile): Future[User] = {
    retrieve(profile.loginInfo).map(maybeUser => {
//      val existingUser = maybeUser.fold(
//        profile.email.flatMap(email => query.getAccounts(filter = _.apply(emails = Seq(email))).execute().headOption.map(toUser(_, profile.loginInfo)))
//      )(u => Some(u))

      maybeUser.fold({
          val insertedAccountResult = query.insertAccount(_.apply(profile.firstName, profile.lastName, profile.email, profile.avatarURL)).execute()
          insertedAccountResult.entity match {
            case Left(account) => {
              query.insertAccountProvider(_.apply(account.id, profile.loginInfo.providerID, profile.loginInfo.providerKey)).execute()
              toUser(account, profile.loginInfo)
            }
            case Right(e) => throw e
          }
        }
      )(user => {
        query.updateAccounts(filter = _.apply(ids = Seq(user.id)), values = _.apply(
          firstName = UpdateValue(profile.firstName),
          lastName = UpdateValue(profile.lastName),
          avatarURL = UpdateValue(profile.avatarURL)
        )).execute()
        user
      })

    })
  }

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    Future(query.getAccountProviders(
      filter = _.apply(providerIdAndProviderKey = Some(_.apply(Seq(loginInfo.providerID), Seq(loginInfo.providerKey)))),
      include = s => Seq(s.account)).execute().headOption.map(toUser(_)))
  }

  private def toUser(accountProvider: AccountProvider): User = {
    toUser(accountProvider.account, LoginInfo(accountProvider.providerId, accountProvider.providerKey))
  }

  private def toUser(account: Account, loginInfo: LoginInfo): User = {
    val firstAndLastSeq = account.firstName.toSeq ++ account.lastName.toSeq
    User(
      account.id,
      loginInfo,
      account.firstName,
      account.lastName,
      if(firstAndLastSeq.nonEmpty) Some(firstAndLastSeq.mkString(" ")) else None,
      account.email,
      account.avatarURL)
  }


}
