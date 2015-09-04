package utils

import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.wechatmeal.data.JsonImplicits
import play.api.Play
import play.api.libs.concurrent.{Akka, Execution}
import play.api.mvc.Controller

/**
 * BaseController
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
trait BaseController extends JsonImplicits with LazyLogging {
  this: Controller =>

  implicit def application = Play.current

  implicit def system = Akka.system

  implicit def __ec = Execution.defaultContext
}
