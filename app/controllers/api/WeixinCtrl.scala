package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.service.WeixinService
import me.yangbajing.wechatmeal.utils.Utils
import me.yangbajing.weixin.mp.message._
import org.apache.commons.lang3.StringUtils
import play.api.mvc.{Action, Controller}
import utils.BaseController

import scala.concurrent.Future
import scala.xml.Elem

/**
 * 连接微信API
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
@Singleton
class WeixinCtrl @Inject()(weixinService: WeixinService) extends Controller with BaseController {
  def get(signature: String, echostr: String, timestamp: String, nonce: String) = Action.async { request =>
    logger.debug(request.rawQueryString)

    weixinService.validateSign(timestamp, nonce).map {
      case `signature` => Ok(echostr)
      case s =>
        logger.error(s"$s not match $signature")
        BadRequest
    }
  }

  def post = Action.async(parse.tolerantText) { request =>
    val timestamp = request.getQueryString("timestamp").get
    val nonce = request.getQueryString("nonce").get
    val encryptType = request.getQueryString("encrypt_type")
    val msgSignature = request.getQueryString("msg_signature")

    def getBody = if (encryptType.contains("aes")) {
      weixinService.descryptMsg(msgSignature.get, timestamp, nonce, request.body)
    } else {
      Future.successful(request.body)
    }

    def responseContent(body: String) = {
      val node = scala.xml.XML.loadString(body)
      OrdinaryMessage.msgType(node) match {
        case MessageTypes.Event => // event
          val event = EventMessage(node)
          if (event.event == EventTypes.Subscribe) {
            getContent(node, "关注纷享，开启高品质生活第一步。")
          } else {
            Future.successful("")
          }

        case MessageTypes.Image =>
          val msg = OrdinaryMessage(node)
          val curSeconds = Utils.currentTimeSeconds()
          val respStr = OrdinaryTextResponse(msg.fromUserName, msg.toUserName, curSeconds, "谢谢上传图片！").stringify()
          weixinService.encryptMsg(respStr, curSeconds, Utils.randomString(8))

        case _ => // message
          getContent(node)
      }
    }

    for {
      body <- getBody
      resp <- responseContent(body)
    } yield {
      Ok(resp).withHeaders("Content-Type" -> "application/xml; charset=UTF-8")
    }
  }

  private def getContent(node: Elem, content: String = ""): Future[String] = {
    val msg = OrdinaryMessage(node)
    val newTs = Utils.currentTimeSeconds()
    val replyContent =
      if (StringUtils.isEmpty(content)) {
        "欢迎关注羊八井花园"
      } else {
        content
      }
    val respStr = OrdinaryTextResponse(msg.fromUserName, msg.toUserName, newTs, replyContent).stringify()
    weixinService.encryptMsg(respStr, newTs, Utils.randomString(8))
  }
}
