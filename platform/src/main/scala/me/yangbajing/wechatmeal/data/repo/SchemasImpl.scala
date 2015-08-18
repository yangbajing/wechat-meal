package me.yangbajing.wechatmeal.data.repo

import java.net.URI
import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.WeixinAccount
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
 * Schemas
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
@Singleton
class SchemasImpl @Inject()(lifecycle: ApplicationLifecycle) extends Schemas {
  println("SchemasImpl init")

  // 整个play应用停止时的回调函数，用于关闭Database连接
  lifecycle.addStopHook(() => Future.successful {
    logger.info("close Database")
    db.close()
  })
}

@ImplementedBy(classOf[SchemasImpl])
trait Schemas extends StrictLogging {
  val db = {
    val (dbUrl, username, password) =
      Option(System.getenv("DATABASE_URL")) match {
        case Some(databaseUrl) =>
          val dbUri = new URI(databaseUrl)
          val username = dbUri.getUserInfo.split(":")(0)
          val password = dbUri.getUserInfo.split(":")(1)
          val dbUrl = "jdbc:postgresql://" + dbUri.getHost + ':' + dbUri.getPort + dbUri.getPath
          (dbUrl, username, password)
        case None =>
          val c = ConfigFactory.load().getConfig("wechat-meal.db")
          (c.getString("dbUrl"), c.getString("username"), c.getString("password"))
      }

    Database.forURL(dbUrl, username, password, driver = "org.postgresql.Driver",
      executor = AsyncExecutor("WechatMeal", numThreads = 2, queueSize = 1000))
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
