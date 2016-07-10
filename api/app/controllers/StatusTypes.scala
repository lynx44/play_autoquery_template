package controllers

import play.api.Logger
import play.api.http.{HttpEntity, Writeable}
import play.api.libs.json.{JsValue, JsResult, Reads, JsReadable}
import play.api.mvc.Results.Status
import play.api.mvc._

import scala.concurrent.Future
import scala.reflect.runtime.universe._

object StatusTypes extends StatusTypes

trait StatusTypes extends Results {
  class ValidatedStatus(status: Int) extends Status(status) {
    def withContent[C](content: C)(implicit writeable: Writeable[C]): ValidatedContentStatus[C] = {
      new ValidatedContentStatus[C](status, apply(content).body)
    }
  }

  class ValidatedContentStatus[C](status: Int, override val body: HttpEntity) extends ValidatedStatus(status)
//  type ValidatedResult = play.mvc.Result
//  class ValidatedResultTyped[R[A]](status: Int) extends ValidatedStatus(status) {
//    override def apply[C](content: C)(implicit writeable: Writeable[C]): R = {
//      super.apply(content).asInstanceOf[R]
//    }
//  }

//  class ContentResult[T](result: Result) extends ValidatedResult(result.res)
//  type ValidatedResult[T] = Status with
  override val Ok: OkStatus = new OkStatus()
//  def OkWithContent[C]: OkWithContent[C] = new OkWithContent[C]()
  override val Unauthorized: UnauthorizedStatus = new UnauthorizedStatus()
  override val BadRequest: BadRequestStatus = new BadRequestStatus()

  implicit def result2_1[S1 <: ValidatedStatus, S2 <: ValidatedStatus](s1: S1)(implicit s1tag: TypeTag[S1]): StatusOf2[S1, S2] = {
    new StatusOf2[S1, S2](Some(s1), None)
  }

  implicit def result2_2[S1 <: ValidatedStatus, S2 <: ValidatedStatus](s2: S2)(implicit s2tag: TypeTag[S2]): StatusOf2[S1, S2] = {
    new StatusOf2[S1, S2](None, Some(s2))
  }

//  class OkResult[T] extends ValidatedResult(play.api.http.Status.OK)
  class OkStatus() extends ValidatedStatus(play.api.http.Status.OK) {
  override def withContent[C](content: C)(implicit writeable: Writeable[C]): OkWithContent[C] =
      new OkWithContent[C](super.withContent(content))
  }
  class OkWithContent[C](content: ValidatedContentStatus[C]) extends ValidatedContentStatus[C](play.api.http.Status.OK, content.body)
  class UnauthorizedStatus() extends ValidatedStatus(play.api.http.Status.UNAUTHORIZED)
  class BadRequestStatus() extends ValidatedStatus(play.api.http.Status.BAD_REQUEST)
  trait StatusOf {
    def result: Result
  }

//  trait ValidatedJsValue[T] {
//    def value: JsValue
//  }
//  case class ValidatedJsValueImpl[T](value: JsValue)
//
//  object Json {
//    def toJson[T](o : T)(implicit tjs : play.api.libs.json.Writes[T]) : ValidatedJsValue[T] = new ValidatedJsValueImpl[T](play.api.libs.json.Json.toJson(o))
//  }

//  class ValidatedActionResult1[T, S1 <: ValidatedResult](action: ValidatedResult) extends Action[T] {
//    def parser: BodyParser[T] = action.parser
//
//    def apply(request: Request[T]): Future[Result] = action.apply(request)
//  }
//
  class ValidatedActionResult2[T, S1 <: ValidatedStatus, S2 <: ValidatedStatus](action: Action[T]) extends Action[T] {
    def parser: BodyParser[T] = action.parser

    def apply(request: Request[T]): Future[Result] = action.apply(request)
  }

  class StatusOf2[S1 <: ValidatedStatus, S2 <: ValidatedStatus](s1: Option[S1], s2: Option[S2]) extends StatusOf {

    def result = s1.getOrElse(s2.get)
  }

  implicit def withValidated[R[A]](action: play.api.mvc.ActionBuilder[R]) = {
    WithValidated(ValidatedActionImpl(action))
  }

  case class WithValidated[R[A]](validated: ValidatedAction[R])
  case class ValidatedActionImpl[R[A]](override val action: play.api.mvc.ActionBuilder[R]) extends ValidatedAction[R]

//  object ValidatedAction extends ValidatedAction[Request] {
//    override def action = Action
//  }

  trait ValidatedAction[+R[_]] {
    def action: play.api.mvc.ActionBuilder[R]

    def apply[A, S1 <: ValidatedStatus](bodyParser: BodyParser[A])(block: R[A] => S1)(implicit s1tag: TypeTag[S1]): Action[A] = action.async(bodyParser) { req: R[A] =>
      Future.successful(block(req))
    }

    def apply[A, S1 <: ValidatedStatus, S2 <: ValidatedStatus](bodyParser: BodyParser[A])(block: R[A] => StatusOf2[S1, S2])(implicit s1tag: TypeTag[S1], s2tag: TypeTag[S2]): ValidatedActionResult2[A, S1, S2] =
      new ValidatedActionResult2[A, S1, S2](action.async(bodyParser) { req: R[A] =>
        Future.successful(block(req).result)
      })

//    def apply[A, S1 <: ValidatedResult, S2 <: ValidatedResult](bodyParser: BodyParser[A])(block: R[A] => StatusOf2[S1, S2])(implicit s1tag: TypeTag[S1], s2tag: TypeTag[S2]): ValidatedActionResult2[A, S1, S2] =
//      new ValidatedActionResult2[A, S1, S2](action.async(bodyParser) { req: R[A] =>
//        Future.successful(block(req).result)
//      })
  }
}

