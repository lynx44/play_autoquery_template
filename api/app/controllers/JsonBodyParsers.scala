package controllers

import org.example.project.rest.models.{JsonParser, UpickleJsonService, JsonService}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

trait JsonBodyParsers extends BodyParsers {
  implicit class BodyParserExtension(bodyParsers: this.parse.type) {
    def asJson[T](implicit reader: JsonParser[T]): BodyParser[T] = {
      import play.api.libs.iteratee.Execution.Implicits.trampoline
      bodyParsers.raw.map(s => {
        val read = reader.read(s.asBytes().map(b => {

          val s1 = new Predef.String(b.toArray)
          s1
        }).getOrElse(null))
        read
      })
    }
  }

  protected def toJson[T](value: T)(implicit writer: JsonParser[T]): JsValue = {
    Json.parse(writer.write(value))
  }
}