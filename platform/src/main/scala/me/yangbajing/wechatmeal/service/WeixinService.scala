package me.yangbajing.wechatmeal.service

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import akka.pattern.ask
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.wechatmeal.common.Settings
import me.yangbajing.wechatmeal.data.model.User
import me.yangbajing.wechatmeal.data.repo.{MenuRepo, Schemas, WeixinAccountRepo}
import me.yangbajing.wechatmeal.service.actors.UserMaster
import me.yangbajing.wechatmeal.service.actors.command.{SetUser, Command, CommandResult}
import me.yangbajing.wechatmeal.utils.Utils
import me.yangbajing.weixin.mp.message.{OrdinaryMessage, OrdinaryResponse, OrdinaryTextResponse}
import org.apache.commons.codec.digest.DigestUtils
import play.api.Play.current
import play.api.cache.CacheApi
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext

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
                              settings: Settings,
                              cacheApi: CacheApi) extends LazyLogging {
  private val accountFuture = weixinAccountRepo.findOneById("wechat_meal").map { v =>
    v.getOrElse(throw new IllegalStateException("WeixinAccount: wechat_meal not found")).toAccount
  }
  private val wxFuture = accountFuture.map(account =>
    new WXBizMsgCrypt(account.token, account.encodingAESKey, account.appId))

  private val userMaster = Akka.system.actorOf(UserMaster.props(schemas, cacheApi), "user-master")

  menuRepo.findOneByDate(Utils.nowDate()).onSuccess {
    case Some(menu) =>
      val key = "menu-" + Utils.nowDate()
      logger.info(s"生成当日菜单 $key：$menu")
      cacheApi.set(key, menu, Duration(24, TimeUnit.HOURS))
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
    (userMaster ? Command(message.fromUserName, message.content.trim)).mapTo[CommandResult].map { result =>
      OrdinaryTextResponse(message.fromUserName, message.toUserName, Utils.currentTimeSeconds(), result.content)
    }
  }

  def updateUser(user: User) = {
    userMaster ! SetUser(user)
  }

}
