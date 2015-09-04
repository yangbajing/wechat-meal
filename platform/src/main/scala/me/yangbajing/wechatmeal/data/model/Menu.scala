package me.yangbajing.wechatmeal.data.model

import java.time.{LocalDate, ZonedDateTime}

import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.JsonImplicits
import me.yangbajing.wechatmeal.data.domain.MenuItem
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.utils.Utils
import play.api.libs.json.JsValue

/**
 * Menu
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
case class Menu(id: Long,
                `type`: MealType.MealType,
                date: LocalDate,
                menu: JsValue,
                createdAt: ZonedDateTime) {

  def prettyString() =
    menus.zipWithIndex.map { case (item, idx) =>
      (idx + 1) + ": " + item.name + " ￥" + item.price
    }.mkString(Utils.nowDate() + "菜单", "\n", "")

  import JsonImplicits.__menuItemFormat

  lazy val menus: Vector[MenuItem] = menu.as[Vector[MenuItem]]
}

class TableMenu(tag: Tag) extends Table[Menu](tag, "t_menu") {
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val `type` = column[MealType.MealType]("type")
  val date = column[LocalDate]("date")
  val menu = column[JsValue]("menu")
  val createdAt = column[ZonedDateTime]("createdAt")

  def * = (id, `type`, date, menu, createdAt) <>(Menu.tupled, Menu.unapply)
}
