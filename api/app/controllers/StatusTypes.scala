package controllers

import play.api.Logger
import play.api.mvc.Results.Status
import play.api.mvc._

import scala.concurrent.Future
import scala.reflect.runtime.universe._

object StatusTypes extends StatusTypes

trait StatusTypes extends Results {
  type ValidatedResult = Status
  override val Ok: OkStatus = new OkStatus()
  override val Unauthorized: UnauthorizedStatus = new UnauthorizedStatus()
  override val BadRequest: BadRequestStatus = new BadRequestStatus()

  implicit def result2_1[S1 <: ValidatedResult, S2 <: ValidatedResult](s1: S1)(implicit s1tag: TypeTag[S1]): StatusOf2[S1, S2] = {
    new StatusOf2[S1, S2](Some(s1), None)
  }

  implicit def result2_2[S1 <: ValidatedResult, S2 <: ValidatedResult](s2: S2)(implicit s2tag: TypeTag[S2]): StatusOf2[S1, S2] = {
    new StatusOf2[S1, S2](None, Some(s2))
  }

  class OkStatus() extends ValidatedResult(play.api.http.Status.OK)
  class UnauthorizedStatus() extends ValidatedResult(play.api.http.Status.UNAUTHORIZED)
  class BadRequestStatus() extends ValidatedResult(play.api.http.Status.BAD_REQUEST)
  trait StatusOf {
    def result: Result
  }
  class StatusOf2[S1 <: ValidatedResult, S2 <: ValidatedResult](s1: Option[S1], s2: Option[S2]) extends StatusOf {

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

    def apply[A, S1 <: ValidatedResult](bodyParser: BodyParser[A])(block: R[A] => S1)(implicit s1tag: TypeTag[S1]): Action[A] = action.async(bodyParser) { req: R[A] =>
      Future.successful(block(req))
    }

    def apply[A, S1 <: ValidatedResult, S2 <: ValidatedResult](bodyParser: BodyParser[A])(block: R[A] => StatusOf2[S1, S2])(implicit s1tag: TypeTag[S1], s2tag: TypeTag[S2]): Action[A] = action.async(bodyParser) { req: R[A] =>
      Future.successful(block(req).result)
    }
  }
}

