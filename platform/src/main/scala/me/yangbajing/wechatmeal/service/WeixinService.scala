package me.yangbajing.wechatmeal.service

import javax.inject.Singleton

import com.qq.weixin.mp.aes.WXBizMsgCrypt
import com.typesafe.scalalogging.LazyLogging
import me.yangbajing.weixin.mp.setting.{WeixinSettings, WeixinSetting}
import org.apache.commons.codec.digest.DigestUtils
import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
 * 微信接口服务
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
@Singleton
class WeixinService extends LazyLogging {
  val setting = WeixinSettings("wechat-meal.weixin")
  val wx = new WXBizMsgCrypt(setting.token, setting.encodingAESKey, setting.appId)

  def validateSign(timestamp: String, nonce: String) = {
    DigestUtils.sha1Hex(Seq(setting.token, timestamp, nonce).sorted.mkString)
  }

  def encryptMsg(replyMsg: String, timestamp: Long, nonce: String): String = {
    logger.debug("encryptMsg before:\n" + replyMsg)
    val result = wx.encryptMsg(replyMsg, timestamp.toString, nonce)
    logger.debug("encryptMsg after:\n" + result)
    result
  }

  def descryptMsg(msgSignature: String, timestamp: String, nonce: String, postData: String): String = {
    //    logger.debug(s"msgSignature: $msgSignature\ntimestamp: $timestamp\nnonce: $nonce\npostData:\n$postData")
    val result = wx.decryptMsg(msgSignature, timestamp, nonce, postData)
    logger.debug("descryptMsg after:\n" + result)
    result
  }

}
