package me.yangbajing.weixin.mp.setting

import java.time.Duration
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging

case class WeixinApi(accessToken: String,
                     getcallbackip: String,
                     materialGetMaterial: String,
                     materialGetMaterialcount: String,
                     materialPatchgetMaterial: String,
                     mediaGet: String,
                     ticketGetticket: String)

object WeixinApi {
  def apply(conf: Config): WeixinApi = WeixinApi(
    conf.getString("access-token"),
    conf.getString("getcallbackip"),
    conf.getString("material-get_material"),
    conf.getString("material-get_materialcount"),
    conf.getString("material-batchget_material"),
    conf.getString("media-get"),
    conf.getString("ticket-getticket")
  )
}

case class WeixinSetting(timeout: Duration, api: WeixinApi) {
  implicit val akkaTimeout = Timeout(timeout.toMillis, TimeUnit.MILLISECONDS)
}

object WeixinSettings extends StrictLogging {
  def apply(conf: String): WeixinSetting = {
    apply(ConfigFactory.load().getConfig(conf))
  }

  def apply(conf: Config): WeixinSetting = {
    WeixinSetting(conf.getDuration("timeout"), WeixinApi(conf.getConfig("api")))
  }
}
