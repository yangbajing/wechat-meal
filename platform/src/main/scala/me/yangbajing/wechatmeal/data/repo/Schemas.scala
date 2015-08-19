package me.yangbajing.wechatmeal.data.repo

import java.net.URI
import java.time.{LocalDate, ZonedDateTime}
import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model._
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.JsValue

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

  class TableUser(tag: Tag) extends Table[User](tag, "t_user") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val openid = column[Option[String]]("openid")
    val email = column[String]("email")
    val createdAt = column[ZonedDateTime]("createdAt")

    def * = (id, openid, email, createdAt) <>(User.tupled, User.unapply)
  }

  val tUser = TableQuery[TableUser]

  class TableMerchant(tag: Tag) extends Table[Merchant](tag, "t_merchant") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name = column[String]("name")
    val address = column[String]("address")
    val remark = column[Option[String]]("remark")
    val createdAt = column[ZonedDateTime]("createdAt")

    def * = (id, name, address, remark, createdAt) <>(Merchant.tupled, Merchant.unapply)
  }

  val tMerchant = TableQuery[TableMerchant]

  class TableMeal(tag: Tag) extends Table[Meal](tag, "t_meal") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val merchantId = column[Int]("merchantId")
    val name = column[String]("name")
    val price = column[BigDecimal]("price")
    val images = column[List[String]]("images")
    val remark = column[Option[String]]("remark")
    val createdAt = column[ZonedDateTime]("createdAt")

    def __fkMerchant = foreignKey(tableName + "_fk_" + tMerchant.baseTableRow.tableName, merchantId, tMerchant)(_.id)

    def * = (id, merchantId, name, price, images, remark, createdAt) <>(Meal.tupled, Meal.unapply)
  }

  val tMeal = TableQuery[TableMeal]

  class TableMenu(tag: Tag) extends Table[Menu](tag, "t_menu") {
    val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val merchantId = column[Int]("merchantId")
    val `type` = column[MealType.MealType]("type")
    val date = column[LocalDate]("date")
    val menu = column[JsValue]("menu")
    val createdAt = column[ZonedDateTime]("createdAt")

    def __fkMerchant = foreignKey(tableName + "_fk_" + tMerchant.baseTableRow.tableName, merchantId, tMerchant)(_.id)

    def * = (id, merchantId, `type`, date, menu, createdAt) <>(Menu.tupled, Menu.unapply)
  }

  val tMenu = TableQuery[TableMenu]

  def schemas =
    tWeixinAccount.schema ++
      tUser.schema ++
      tMerchant.schema ++
      tMeal.schema ++
      tMenu.schema
}
