package me.yangbajing.wechatmeal.data.repo

import com.typesafe.config.ConfigFactory
import me.yangbajing.wechatmeal.data.SchemaUnitSpec

/**
 * SchemasTest
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
class SchemasTest extends SchemaUnitSpec {

  "SchemasTest" should {
    "create tables statements" in {
      val config = ConfigFactory.load()

      println(config.hasPath("wechat-meal.weixin"))

      println(config.getConfig("wechat-meal.weixin"))

      schemas.tableSchemas.createStatements.foreach(s => println(s + ";"))
    }

    //    "create tables" in {
    //      schemas.db.run(schemas.tableSchemas.create.transactionally)
    //    }

    //    "insert statements" in {
    //      val wa = WeixinAccount("wechat_meal", "wxce9fa3f24a064072", "547d2259af7497661db9f600302451a4", "ZGRkNGZjODY0NDlmZjFmZGExYzdmODdm", "")
    //      val q = schemas.tWeixinAccount += wa
    //      q.statements.foreach(println)
    //    }
  }

}
