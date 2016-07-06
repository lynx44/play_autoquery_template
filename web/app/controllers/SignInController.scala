package controllers

import javax.inject.Inject

import com.standardedge.http.{HttpResponseCode, URL, HttpRequestDSL}
import forms.SignInForm
import org.example.project.rest.models.JsonParsers
import org.example.project.rest.models.auth.LoginResource
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Result, Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class SignInController @Inject()(
  val messagesApi: MessagesApi)(
  implicit httpRequestDSL: HttpRequestDSL,
  jsonParsers: JsonParsers,
  executionContext: ExecutionContext,
  val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  import httpRequestDSL._
  import jsonParsers._

  def view = Action.async { implicit request =>
    Future.successful(Ok(views.html.signIn(SignInForm.form)))
  }

  def submit = Action.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signIn(form))),
      data => {
        val response = future(POST(URL("http://localhost:9001/auth/login"))
          .setBody(LoginResource(data.email, data.password, "")))

        response.map(r => r.handleJSONResponse[Unit, Result] {
            case Some((responseCode, validation)) => responseCode match {
              case HttpResponseCode.Ok => Ok
              case _ => Unauthorized
            }
            case None => Unauthorized
          }
        )
      }
    )
  }
}
