package org.example.project.rest.models

import org.example.project.rest.models.auth.{AccountCreateResource, LoginResource}

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

trait JsonParser[T] {
  def read(json: String): T
  def write(obj: T): String
}

case class UpickleJsonParser[T](implicit readerWriter: upickle.default.ReadWriter[T]) extends JsonParser[T] {
  def read(json: String): T = readerWriter.read(upickle.json.read(json))

  def write(obj: T): String = readerWriter.write(obj).toString()
}
