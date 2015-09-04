package me.yangbajing.wechatmeal.service

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.model.Menu
import me.yangbajing.wechatmeal.data.repo.MenuRepo
import me.yangbajing.wechatmeal.utils.Utils
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration._

/**
 * Menu service
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-04.
 */
@Singleton
class MenuService @Inject()(menuRepo: MenuRepo,
                            cacheApi: CacheApi) {
  def findOneById(menuId: Long) = menuRepo.findOneById(menuId)

  def insert(menu: Menu) = {
    menuRepo.insert(menu).map { id =>
      cacheApi.set("menu-" + Utils.nowDate(), menu, 24.hours)
      id
    }
  }
}
