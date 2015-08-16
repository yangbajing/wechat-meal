package me.yangbajing.wechatmeal.data.repo

import com.typesafe.config.ConfigFactory
import me.yangbajing.wechatmeal.data.SchemaUnitSpec
import me.yangbajing.wechatmeal.data.model.WeixinAccount
import org.scalatest.WordSpec
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._

/**
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
class SchemasTest extends WordSpec with SchemaUnitSpec {
  override protected def afterAll(): Unit = {
    schemas.db.close()
  }

  "SchemasTest" should {
    "create tables" in {
      val config = ConfigFactory.load()

      println(config.hasPath("wechat-meal.weixin"))

      println(config.getConfig("wechat-meal.weixin"))

      schemas.schemas.createStatements.foreach(println)
    }

    "insert statements" in {
      val wa = WeixinAccount("wechat_meal", "wxce9fa3f24a064072", "547d2259af7497661db9f600302451a4", "ZGRkNGZjODY0NDlmZjFmZGExYzdmODdm", "")
      val q = schemas.tWeixinAccount += wa
      q.statements.foreach(println)
    }
  }
}
