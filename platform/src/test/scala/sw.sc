import com.typesafe.config.ConfigFactory
import me.yangbajing.wechatmeal.utils.Utils

val config = ConfigFactory.load()

config.hasPath("wechat-meal")

config.getConfig("weixin")