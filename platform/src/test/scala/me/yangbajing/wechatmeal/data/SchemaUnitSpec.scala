package me.yangbajing.wechatmeal.data

import me.yangbajing.wechatmeal.data.repo.{WeixinAccountRepo, Schemas}
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * SchemaUnitSpec
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
trait SchemaUnitSpec extends BeforeAndAfterAll {
  this: Suite =>

  val schemas = new Schemas()
  val weixinAccountRepo = new WeixinAccountRepo(schemas)

  override protected def afterAll(): Unit = {
    schemas.db.close()
  }
}
