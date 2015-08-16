package me.yangbajing.wechatmeal.data.repo

import java.net.URI
import javax.inject.Singleton

import com.typesafe.config.ConfigFactory
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.WeixinAccount

/**
 * Schemas
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
@Singleton
class Schemas {
  val db = {
    val (dbUrl, username, password) =
      Option(new URI(System.getenv("DATABASE_URL"))) match {
        case Some(dbUri) =>
          val username = dbUri.getUserInfo.split(":")(0)
          val password = dbUri.getUserInfo.split(":")(0)
          val dbUrl = "jdbc:postgresql://" + dbUri.getHost + ':' + dbUri.getPort + dbUri.getPath
          (dbUrl, username, password)
        case None =>
          val c = ConfigFactory.load().getConfig("wechat-meal.db")
          (c.getString("dbUrl"), c.getString("username"), c.getString("password"))
      }

    Database.forURL(dbUrl, username, password, driver = "org.postgresql.Driver",
      executor = AsyncExecutor("WechatMeal", numThreads = 10, queueSize = 1000))
  }

  class TableWeixinAccount(tag: Tag) extends Table[WeixinAccount](tag, "t_weixin_account") {
    val id = column[String]("id", O.PrimaryKey)
    val appId = column[String]("appId")
    val appSecret = column[String]("appSecret")
    val token = column[String]("token")
    val encodingAESKey = column[String]("encodingAESKey", O.Default(""))

    def * = (id, appId, appSecret, token, encodingAESKey) <>(WeixinAccount.tupled, WeixinAccount.unapply)
  }

  val tWeixinAccount = TableQuery[TableWeixinAccount]

  def schemas =
    tWeixinAccount.schema
}
