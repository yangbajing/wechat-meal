package me.yangbajing.wechatmeal.service

import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import akka.pattern.ask
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.wechatmeal.common.Settings
import me.yangbajing.wechatmeal.data.repo.{Schemas, MenuRepo, WeixinAccountRepo}
import me.yangbajing.wechatmeal.service.actors.Commands.{CommandResult, Command}
import me.yangbajing.wechatmeal.service.actors.{UserMaster, Commands}
import me.yangbajing.wechatmeal.utils.Utils
import me.yangbajing.weixin.mp.message.{OrdinaryTextResponse, OrdinaryResponse, OrdinaryMessage}
import org.apache.commons.codec.digest.DigestUtils
import play.api.cache.Cache
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/**
 * 微信接口服务
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
@Singleton
class WeixinService @Inject()(schemas: Schemas,
                              weixinAccountRepo: WeixinAccountRepo,
                              menuRepo: MenuRepo,
                              settings: Settings) extends LazyLogging {
  private val accountFuture = weixinAccountRepo.findOneById("wechat_meal").map { v =>
    v.getOrElse(throw new IllegalStateException("WeixinAccount: wechat_meal not found")).toAccount
  }
  private val wxFuture = accountFuture.map(account =>
    new WXBizMsgCrypt(account.token, account.encodingAESKey, account.appId))

  private val userMaster = Akka.system.actorOf(UserMaster.props(schemas), "user-master")

  menuRepo.findCurrentMenu().onSuccess {
    case Some(menu) =>
      val key = "menu-" + Utils.nowDate()
      logger.info(s"生成当日菜单 $key：$menu")
      Cache.set(key, menu, Duration(24, TimeUnit.SECONDS))

    case None =>
      logger.warn("当日菜单未找到")
  }

  def validateSign(timestamp: String, nonce: String) = accountFuture.map(account =>
    DigestUtils.sha1Hex(Seq(account.token, timestamp, nonce).sorted.mkString))

  def encryptMsg(replyMsg: String, timestamp: Long, nonce: String): Future[String] = wxFuture.map { wx =>
    logger.debug("encryptMsg before:\n" + replyMsg)
    val result = wx.encryptMsg(replyMsg, timestamp.toString, nonce)
    logger.debug("encryptMsg after:\n" + result)
    result
  }

  def descryptMsg(msgSignature: String,
                  timestamp: String,
                  nonce: String,
                  postData: String): Future[String] = wxFuture.map { wx =>
    // logger.debug(s"msgSignature: $msgSignature\ntimestamp: $timestamp\nnonce: $nonce\npostData:\n$postData")
    val result = wx.decryptMsg(msgSignature, timestamp, nonce, postData)
    logger.debug("descryptMsg after:\n" + result)
    result
  }

  def commandTextMsg(message: OrdinaryMessage): Future[OrdinaryResponse] = {
    import me.yangbajing.wechatmeal.utils.Utils.timeout
    (userMaster ? Command(message.fromUserName, message.content)).mapTo[CommandResult].map { result =>
      OrdinaryTextResponse(message.fromUserName, message.toUserName, Utils.currentTimeSeconds(), result.content)
    }

    //    menuRepo.findCurrentMenu().map {
    //      case None =>
    //        "当日菜单未生成，请稍后查看。谢谢！"
    //
    //      case Some(menu) =>
    //        menu.menus.zipWithIndex.map { case (item, idx) =>
    //          s"${idx + 1}：${item.name} (￥${item.price}"
    //        }.mkString("\n")
    //
    //    }.map { content =>
    //      OrdinaryTextResponse(message.fromUserName, message.toUserName, Utils.currentTimeSeconds(), content)
    //    }

    //    val content = message.content match {
    //      case "1" =>
    //        "请输入公司邮箱"
    //
    //      case "2" =>
    //        """1: 鱼香肉丝炒饭
    //          |2: 回锅内炒饭
    //        """.stripMargin
    //
    //      case "3" =>
    //        "点餐历史记录未实现"
    //
    //      case _ =>
    //        Commands.COMMAND_HELP
    //    }
  }

}
