package org.example.project.rest.models

import com.standardedge.http.{EmptyJsonException, JsonValidation, JsonWriter, JsonReader}
import org.example.project.rest.models.auth.{AccountCreateResource, LoginResource}

import scala.util.{Failure, Success, Try}

trait JsonService {
  def parserFor[T](implicit jsonParser: JsonParser[T]): JsonParser[T]
}

trait JsonParsers {
  val loginResourcePickler = upickle.default.macroRW[LoginResource]
  implicit lazy val loginResourceParser = UpickleJsonParser[LoginResource]

  val accountCreateResourcePickler = upickle.default.macroRW[AccountCreateResource]
  implicit lazy val accountCreateResourceParser = UpickleJsonParser[AccountCreateResource]
}
object JsonParsers extends JsonParsers

class UpickleJsonService extends JsonService {
  def parserFor[T](implicit jsonParser: JsonParser[T]): JsonParser[T] = jsonParser
}

trait JsonParser[T] extends JsonReader[T] with JsonWriter[T]

case class UpickleJsonParser[T](implicit readerWriter: upickle.default.ReadWriter[T]) extends JsonParser[T] {

  def write(obj: T): String = readerWriter.write(obj).toString()

  def read(body: Option[String]): JsonValidation[T] = {
    body.fold[JsonValidation[T]](JsonReaderInvalid(new EmptyJsonException("JSON body parameter did not return a value")))(
      js => {
        Try(readerWriter.read(upickle.json.read(js))) match {
          case Success(t) => JsonReaderValid(t)
          case Failure(e) => {
            JsonReaderInvalid(e)
          }
        }
      }
    )
  }
}
