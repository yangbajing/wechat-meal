package utils

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Results, WithFilters}
import play.api.{Application, GlobalSettings}

import scala.concurrent.Future

/**
 * 全局配置
 * Created by jingyang on 15/7/17.
 */
object Global extends GlobalSettings with StrictLogging {

  override def onStart(app: Application) = {
    logger.info("Global.onStarting:")
  }

  override def onStop(app: Application) = {
    logger.info("Global.onStopped:")
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    request.path match {
      case s if s.startsWith("/inapi/") =>
        logger.error(ex.toString, ex)
        Future.successful {
          Results.InternalServerError(Json.obj("code" -> -1, "message" -> ex.getLocalizedMessage))
        }

      case _ =>
        super.onError(request, ex)
    }
  }

}
