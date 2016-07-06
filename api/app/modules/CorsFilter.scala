package modules

import javax.inject.Inject

import akka.stream.Materializer
import play.api.http.HeaderNames._
import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

class CorsFilter @Inject()(
                            implicit override val mat: Materializer,
                            exec: ExecutionContext) extends Filter {
  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val origin = request.headers.get(ORIGIN).getOrElse("*")
    if(request.method == "OPTIONS") {
      val response = Ok.withHeaders(
        ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
        ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, OPTIONS",
        ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, $CONTENT_TYPE, $ACCEPT, $REFERER, $ACCEPT_ENCODING, $ACCEPT_LANGUAGE, $CACHE_CONTROL, $CONNECTION, $CONTENT_LENGTH, $COOKIE, $HOST, $USER_AGENT"
      )
      Future.successful(response)
    } else {
      next(request).map {
        res => res.withHeaders(
          ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
          ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, $CONTENT_TYPE, $ACCEPT, $REFERER, $ACCEPT_ENCODING, $ACCEPT_LANGUAGE, $CACHE_CONTROL, $CONNECTION, $CONTENT_LENGTH, $COOKIE, $HOST, $USER_AGENT"
        )
      }
    }
  }
}
