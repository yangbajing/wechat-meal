package me.yangbajing.wechatmeal.data.model

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.weixin.mp.setting.SettingAccount

/**
 * WeixinAccount
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
case class WeixinAccount(id: String,
                         appId: String,
                         appSecret: String,
                         token: String,
                         encodingAESKey: String) {
  def toAccount = SettingAccount(appId, appSecret, token, encodingAESKey)
}

class TableWeixinAccount(tag: Tag) extends Table[WeixinAccount](tag, "t_weixin_account") {
  val id = column[String]("id", O.PrimaryKey)
  val appId = column[String]("appId")
  val appSecret = column[String]("appSecret")
  val token = column[String]("token")
  val encodingAESKey = column[String]("encodingAESKey", O.Default(""))

  def * = (id, appId, appSecret, token, encodingAESKey) <>(WeixinAccount.tupled, WeixinAccount.unapply)
}