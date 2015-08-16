package me.yangbajing.wechatmeal.data.model

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
