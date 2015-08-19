package me.yangbajing.wechatmeal.data.driver

import com.github.tminglei.slickpg.{ExPostgresDriver, PgArraySupport, PgDate2Support}

/**
 * My Postgres Driver
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object MyDriver
  extends ExPostgresDriver
  with PgDate2Support
  with PgArraySupport {

  override val api = MyAPI

  object MyAPI
    extends API
    with DateTimeImplicits
    with ArrayImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}
