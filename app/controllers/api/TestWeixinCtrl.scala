package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.service.WeixinService
import me.yangbajing.weixin.mp.message.{OrdinaryTextResponse, OrdinaryMessage}
import play.api.mvc.{Action, Controller}
import utils.BaseController

import scala.xml.PCData

/**
 * TestWeixinCtrl
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
@Singleton
class TestWeixinCtrl @Inject()(weixinService: WeixinService) extends Controller with BaseController {

  def post = Action.async(parse.tolerantText) { request =>

    val msg = OrdinaryMessage(
      <xml>
        <MsgType>text</MsgType>
        <FromUserName>yangbajing</FromUserName>
        <Content>
          {PCData(request.body)}
        </Content>
      </xml>)
    weixinService.commandTextMsg(msg).map {
      case result: OrdinaryTextResponse =>
        Ok(result.content)
      case result =>
        Ok(result.stringify()).withHeaders("Content-Type" -> "application/xml; charset=UTF-8")
    }
  }

}
