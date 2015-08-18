package me.yangbajing.wechatmeal.service

import javax.inject.{Inject, Singleton}

import com.qq.weixin.mp.aes.WXBizMsgCrypt
import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.wechatmeal.common.Settings
import me.yangbajing.wechatmeal.data.repo.WeixinAccountRepo
import me.yangbajing.wechatmeal.utils.Utils
import me.yangbajing.weixin.mp.message.{OrdinaryTextResponse, OrdinaryResponse, OrdinaryMessage}
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

  def commandTextMsg(message: OrdinaryMessage): Future[OrdinaryResponse] = {
    val content = message.content match {
      case "1" =>
        "请输入公司邮箱"

      case "2" =>
        """1: 鱼香肉丝炒饭
          |2: 回锅内炒饭
        """.stripMargin

      case "3" =>
        "暂无记录"

      case _ =>
        """?: 返回命令菜单
          |1: 关联账号
          |2: 今日菜单
          |3: 点菜记录
        """.stripMargin
    }
    Future {
      OrdinaryTextResponse(message.fromUserName, message.toUserName, Utils.currentTimeSeconds(), content)
    }
  }
}
