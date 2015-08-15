package utils

import com.typesafe.scalalogging.LazyLogging
import play.api.Play
import play.api.libs.concurrent.Akka
import play.api.mvc.Controller

/**
 * BaseController
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
trait BaseController extends LazyLogging {
  this: Controller =>

  implicit def application = Play.current

  implicit def system = Akka.system

  implicit def __ec = system.dispatcher
}
