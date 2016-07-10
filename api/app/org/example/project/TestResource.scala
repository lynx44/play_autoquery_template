package org.example.project

import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

case class TestResource(email: String, name: String)
object TestResource {
  implicit val testReads: Reads[TestResource] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "name").read[String]
    )(TestResource.apply _)
}

