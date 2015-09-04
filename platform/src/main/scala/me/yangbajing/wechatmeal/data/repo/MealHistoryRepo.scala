package me.yangbajing.wechatmeal.data.repo

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.MealHistory

import scala.concurrent.{ExecutionContext, Future}

/**
 * 订餐历史
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
@Singleton
class MealHistoryRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneByDate(userId: Long, date: LocalDate)(implicit ec: ExecutionContext): Future[Option[MealHistory]] = {
    val q = tMealHistory.filter(t => t.date === date && t.userId === userId).result
    db.run(q).map(_.headOption)
  }

  def findByDate(date: LocalDate): Future[Seq[MealHistory]] = {
    db.run(tMealHistory.filter(_.date === date).result)
  }

  def insert(payload: MealHistory): Future[Int] = {
    val action = (tMealHistory += payload).transactionally
    db.run(action)
  }
}

object MealHistoryRepo {
  def apply(schemas: Schemas): MealHistoryRepo = new MealHistoryRepo(schemas)
}