package me.yangbajing.wechatmeal.data.repo

import javax.inject.Inject

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.Merchant

import scala.concurrent.{ExecutionContext, Future}

/**
 * MerchantRepo
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-18.
 */
class MerchantRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneById(id: Int)(implicit ec: ExecutionContext): Future[Option[Merchant]] = {
    db.run(tMerchant.filter(_.id === id).result).map(_.headOption)
  }
}
