package me.yangbajing.wechatmeal.service

import javax.inject.{Inject, Singleton}

import com.qq.weixin.mp.aes.WXBizMsgCrypt
import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.wechatmeal.common.Settings
import me.yangbajing.wechatmeal.data.repo.WeixinAccountRepo
import org.apache.commons.codec.digest.DigestUtils
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/**
 * 微信接口服务
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
@Singleton
class WeixinService @Inject()(weixinAccountRepo: WeixinAccountRepo,
                              settings: Settings) extends LazyLogging {
  private val accountFuture = weixinAccountRepo.findOneById("wechat_meal").map { v =>
    v.getOrElse(throw new IllegalStateException("WeixinAccount: wechat_meal not found")).toAccount
  }
  private val wxFuture = accountFuture.map(account =>
    new WXBizMsgCrypt(account.token, account.encodingAESKey, account.appId))

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

}
