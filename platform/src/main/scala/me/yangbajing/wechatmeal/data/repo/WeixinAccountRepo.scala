package me.yangbajing.wechatmeal.data.repo

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.model.WeixinAccount
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import scala.concurrent.{Future, ExecutionContext}

/**
 * WeixinAccountRepo
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
@Singleton
class WeixinAccountRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneById(id: String)(implicit e: ExecutionContext): Future[Option[WeixinAccount]] = {
    db.run(tWeixinAccount.filter(_.id === id).result).map(_.headOption)
  }
}
