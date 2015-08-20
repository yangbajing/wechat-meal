package me.yangbajing.wechatmeal.data.domain

import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.JsonImplicits._
import me.yangbajing.wechatmeal.data.model.Menu
import me.yangbajing.wechatmeal.utils.Utils
import org.scalatest.WordSpec
import play.api.libs.json.Json

/**
 * MenuDetailTest
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
class MenuDetailTest extends WordSpec {
  "MenuDetailTest" should {
    "MenuDetail" in {
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

      val menu = Menu(0, 1, MealType.Lunch, Utils.nowDate(), Json.toJson(menus), Utils.now())
      val menuJson = Json.toJson(menu)
      println(Json.stringify(menuJson))
    }
  }
}
