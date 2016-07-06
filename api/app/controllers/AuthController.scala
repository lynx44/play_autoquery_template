package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import forms.SignInForm
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import org.example.project.rest.models.{JsonParsers, JsonService}
import org.example.project.rest.models.auth.LoginResource
import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class AuthController @Inject() (val messagesApi: MessagesApi,
                     silhouette: Silhouette[DefaultEnv],
                     userService: UserService,
                     authInfoRepository: AuthInfoRepository,
                     credentialsProvider: CredentialsProvider,
                     socialProviderRegistry: SocialProviderRegistry,
                     configuration: Configuration,
                     clock: Clock,
                     jsonParsers: JsonParsers) extends Controller with I18nSupport with JsonBodyParsers {

  import jsonParsers._
  def login() = silhouette.UnsecuredAction.async(parse.asJson[LoginResource]) { implicit request => {
    val data = request.body
    val credentials = Credentials(data.username, data.password)
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      val result = Ok
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          val c = configuration.underlying
          silhouette.env.authenticatorService.create(loginInfo).map {
            case authenticator =>
              authenticator.copy(
                expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
              )
          }.flatMap { authenticator =>
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
              silhouette.env.authenticatorService.embed(v, result)
            }
          }
        case None => Future(Unauthorized("Couldn't find user"))
      }
      }.recover {
        case e: ProviderException =>
          Unauthorized(Messages("invalid.credentials"))
      }
    }
  }
}
