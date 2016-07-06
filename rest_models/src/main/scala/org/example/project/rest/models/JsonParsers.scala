package org.example.project.rest.models

import com.standardedge.http.JsonValidation
import org.example.project.rest.models.auth.{AccountCreateResource, LoginResource}

trait JsonParsers {
  val loginResourcePickler = upickle.default.macroRW[LoginResource]
  implicit lazy val loginResourceParser = UpickleJsonParser[LoginResource]

  val accountCreateResourcePickler = upickle.default.macroRW[AccountCreateResource]
  implicit lazy val accountCreateResourceParser = UpickleJsonParser[AccountCreateResource]

  implicit lazy val unitParser = new JsonParser[Unit] {
    def read(body: Option[String]): JsonValidation[Unit] = {
      JsonReaderValid()
    }

    def write(value: Unit): String = ""
  }
}
object JsonParsers extends JsonParsers