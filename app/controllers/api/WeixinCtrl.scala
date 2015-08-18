package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.service.WeixinService
import me.yangbajing.wechatmeal.utils.Utils
import me.yangbajing.weixin.mp.message._
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

    def responseContent(node: Elem): Future[OrdinaryResponse] = OrdinaryMessage.msgType(node) match {
      case MessageTypes.Event => // event
        Future.successful {
          val event = EventMessage(node)
          if (event.event == EventTypes.Subscribe)
            OrdinaryTextResponse(event.fromUserName, event.toUserName, Utils.currentTimeSeconds(),
              "关注羊八井花园，开启高品质生活第一步。")
          else
            OrdinaryEmptyResponse(event.fromUserName, event.toUserName, Utils.currentTimeSeconds())
        }

      case MessageTypes.Text =>
        weixinService.commandTextMsg(OrdinaryMessage(node))

      case _ => // message
        Future.successful {
          val message = OrdinaryMessage(node)
          OrdinaryTextResponse(message.fromUserName, message.toUserName, Utils.currentTimeSeconds(),
            message.contentOption.getOrElse(""))
        }
    }

    def responseResult(resp: OrdinaryResponse): Future[String] =
      weixinService.encryptMsg(resp.stringify(), Utils.currentTimeSeconds(), Utils.randomString(8))

    for {
      body <- getBody
      resp <- responseContent(scala.xml.XML.loadString(body))
      result <- responseResult(resp)
    } yield {
      Ok(result).withHeaders("Content-Type" -> "application/xml; charset=UTF-8")
    }
  }

}
