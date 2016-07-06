package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.{LoginEvent, SignUpEvent, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasher, Clock}
import com.mohiva.play.silhouette.impl.providers.{SocialProviderRegistry, CredentialsProvider}
import forms.SignUpForm
import models.User
import models.services.UserService
import org.example.project.rest.models.JsonParsers
import org.example.project.rest.models.auth.{AccountCreateResource, LoginResource}
import play.api.Configuration
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

class AccountController @Inject() (val messagesApi: MessagesApi,
                                   silhouette: Silhouette[DefaultEnv],
                                   userService: UserService,
                                   authInfoRepository: AuthInfoRepository,
                                   credentialsProvider: CredentialsProvider,
                                   socialProviderRegistry: SocialProviderRegistry,
                                   configuration: Configuration,
                                   clock: Clock,
                                   avatarService: AvatarService,
                                   passwordHasher: PasswordHasher,
                                   jsonParsers: JsonParsers)(
                                   implicit executionContext: ExecutionContext) extends Controller with I18nSupport with JsonBodyParsers {


  import jsonParsers._
  def create() = silhouette.UnsecuredAction.async(parse.asJson[AccountCreateResource]) {
    implicit request => {
      val data = request.body.get
      val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          Future.successful(Conflict)
        case None =>
          val authInfo = passwordHasher.hash(data.password)
          val user = User(
            id = 0,
            loginInfo = loginInfo,
            firstName = data.firstName,
            lastName = data.lastName,
            fullName = Option((data.firstName.toSeq ++ data.lastName.toSeq).mkString(" ")),
            email = Some(data.email),
            avatarURL = None
          )
          for {
            avatar <- avatarService.retrieveURL(data.email)
            user <- userService.save(user.copy(avatarURL = avatar))
            authInfo <- authInfoRepository.add(loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(loginInfo)
            value <- silhouette.env.authenticatorService.init(authenticator)
            result <- silhouette.env.authenticatorService.embed(value, Ok)
          } yield {
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
      }
    }
  }
}
