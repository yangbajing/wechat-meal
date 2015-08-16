package me.yangbajing.wechatmeal.common

import javax.inject.Singleton

import me.yangbajing.weixin.mp.setting.WeixinSettings

/**
 * Settings
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
@Singleton
class Settings {

  val weixinSetting = WeixinSettings("weixin")
}
