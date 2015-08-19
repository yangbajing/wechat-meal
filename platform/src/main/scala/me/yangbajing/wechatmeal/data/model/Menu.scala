package me.yangbajing.wechatmeal.data.model

import java.time.{LocalDate, LocalDateTime}

import me.yangbajing.wechatmeal.common.enums.MealType
import me.yangbajing.wechatmeal.data.domain.MenuItem
import play.api.libs.json.JsValue
import me.yangbajing.wechatmeal.data.JsonImplicits._

/**
 * Menu
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
case class Menu(id: Long,
                merchantId: Int,
                `type`: MealType.MealType,
                date: LocalDate,
                menu: JsValue,
                createdAt: LocalDateTime) {
  lazy val menus: Vector[MenuItem] = menu.as[Vector[MenuItem]]
}
