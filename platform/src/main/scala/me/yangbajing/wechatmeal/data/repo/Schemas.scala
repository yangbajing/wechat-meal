package me.yangbajing.wechatmeal.data.repo

import java.net.URI
import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model._
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
          val Array(un, pwd) = dbUri.getUserInfo.split(":")
          ("jdbc:postgresql://" + dbUri.getHost + ':' + dbUri.getPort + dbUri.getPath, un, pwd)
        case None =>
          val c = ConfigFactory.load().getConfig("wechat-meal.db")
          (c.getString("dbUrl"), c.getString("username"), c.getString("password"))
      }

    Database.forURL(dbUrl, username, password, driver = "org.postgresql.Driver",
      executor = AsyncExecutor("WechatMeal", numThreads = 2, queueSize = 1000))
  }

  val tWeixinAccount = TableQuery[TableWeixinAccount]
  val tUser = TableQuery[TableUser]
  val tCredential = TableQuery[TableCredential]
  val tMenu = TableQuery[TableMenu]

  val tMealHistory = TableQuery[TableMealHistory]

  def tableSchemas =
    tWeixinAccount.schema ++
      tUser.schema ++
      tMenu.schema ++
      tMealHistory.schema
}
