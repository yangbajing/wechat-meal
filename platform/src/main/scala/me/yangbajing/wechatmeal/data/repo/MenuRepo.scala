package me.yangbajing.wechatmeal.data.repo

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.Menu

import scala.concurrent.{ExecutionContext, Future}

/**
 * 菜单
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
@Singleton
class MenuRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneById(menuId: Long)(implicit ec: ExecutionContext) = {
    db.run(tMenu.filter(_.id === menuId).result).map(_.headOption)
  }

  def insert(menu: Menu): Future[Long] = {
    val action = (tMenu returning tMenu.map(_.id) += menu).transactionally
    db.run(action)
  }

  def findOneByDate(date: LocalDate)(implicit ec: ExecutionContext): Future[Option[Menu]] = {
    val q = tMenu.filter(t => t.date === date && t.`type` === MealType.Lunch).sortBy(_.createdAt.desc).result
    db.run(q).map(_.headOption)
  }
}
