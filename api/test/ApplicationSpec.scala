import controllers.AuthController
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox
import scala.reflect._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
//      private val toolBox = scala.reflect.runtime.currentMirror.mkToolBox()
//      toolBox.define(typeOf[AuthController].members.head.asMethod)
//      println(typeOf[AuthController].members.head.name)
      private val testMethod = typeOf[AuthController].members.drop(3).head.asMethod
      println(s"'${testMethod.name}'")

      println(testMethod.paramLists.map(_.name))
//      println(typeOf[AuthController].members.mkString("\n"))
//      println(typeOf[AuthController].members.map(x => (x.name, x.owner)))
      println(typeOf[AuthController].members.filter {
        case m if m.owner.asType.toType =:= typeOf[AuthController] => true
        case _ => false
      }.map(_.name))

      testMethod
      println(typeOf[AuthController].members.drop(1).head.asTerm)
    }

//    "send 404 on a bad request" in new WithApplication{
//      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
//    }
//
//    "render the index page" in new WithApplication{
//      val home = route(FakeRequest(GET, "/")).get
//
//      status(home) must equalTo(OK)
//      contentType(home) must beSome.which(_ == "text/html")
//      contentAsString(home) must contain ("Your new application is ready.")
//    }
  }
}
