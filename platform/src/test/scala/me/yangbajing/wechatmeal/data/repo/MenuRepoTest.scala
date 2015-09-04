package me.yangbajing.wechatmeal.data.repo

import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.SchemaUnitSpec
import me.yangbajing.wechatmeal.data.domain.MenuItem
import me.yangbajing.wechatmeal.data.model.Menu
import me.yangbajing.wechatmeal.utils.Utils
import play.api.libs.json.Json

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * MenuRepoTest
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
class MenuRepoTest extends SchemaUnitSpec {
  val menuRepo = new MenuRepo(schemas)

  "MenuRepoTest" should {
    "insert" in {
      val menus = Seq(
        MenuItem("冬阴功米线", 25),
        MenuItem("咖喱鸡套饭", 25),
        MenuItem("印尼炒饭套", 23),
        MenuItem("泰式猪扒饭", 22),
        MenuItem("泰式炒鲜鱿饭", 24),
        MenuItem("红咖喱牛腩", 28),
        MenuItem("泰式鱼扒饭", 22),
        MenuItem("泰式烧茄子", 18),
        MenuItem("海鲜烧豆腐", 20)
      )

      val menu = Menu(0, MealType.Lunch, Utils.nowDate(), Json.toJson(menus), Utils.now())

      val f = menuRepo.insert(menu)
      Await.result(f, 10.seconds) should be > 0L
    }
  }
}
