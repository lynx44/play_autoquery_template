package controllers

import com.standardedge.http.{JsonWriter, JsonValidation, JsonReader}
import org.example.project.rest.models.{JsonParser, UpickleJsonService, JsonService}
import play.api.libs.json._
import play.api.mvc._

trait JsonParserConversions {
  implicit def readerToPlayReader[T](implicit jsonReader: JsonReader[T]): Reads[T] = {
    new Reads[T] {
      def reads(json: JsValue): JsResult[T] = {
        jsonReader.read(Option(json.toString())).fold(
          invalid => JsError(invalid.toString),
          valid => JsSuccess(valid)
        )
      }
    }
  }

  implicit def writerToPlayWriter[T](implicit jsonWriter: JsonWriter[T]): Writes[T] = {
    new Writes[T] {
      def writes(o: T): JsValue = {
        Json.parse(jsonWriter.write(o))
      }
    }
  }
}