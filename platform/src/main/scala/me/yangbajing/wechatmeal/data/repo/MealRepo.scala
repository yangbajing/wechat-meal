package me.yangbajing.wechatmeal.data.repo

import javax.inject.Inject

import me.yangbajing.wechatmeal.data.model.Meal

import scala.concurrent.{ExecutionContext, Future}
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._

/**
 * MealRepo
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-18.
 */
class MealRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneById(id: Long)(implicit ec: ExecutionContext): Future[Option[Meal]] = {
    db.run(tMeal.filter(_.id === id).result).map(_.headOption)
  }

  def findListByMerchant(merchantId: Int)(implicit ec: ExecutionContext): Future[Seq[Meal]] = {
    db.run(tMeal.filter(_.merchantId === merchantId).result)
  }

  def insert(meal: Meal)(implicit ec: ExecutionContext): Future[Meal] = {
    val u = (tMeal returning tMeal.map(_.id) += meal).transactionally
    db.run(u).map(id => meal.copy(id = id))
  }
}
