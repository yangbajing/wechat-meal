package me.yangbajing.wechatmeal.data.repo

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.Menu
import me.yangbajing.wechatmeal.utils.Utils

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

  def findCurrentMenu()(implicit ec: ExecutionContext): Future[Option[Menu]] = {
    db.run(tMenu.filter(_.date === Utils.nowDate()).result).map(_.headOption)
  }
}
