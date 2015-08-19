package me.yangbajing.wechatmeal.data

import me.yangbajing.wechatmeal.data.domain.MenuItem
import me.yangbajing.wechatmeal.data.model.Menu
import play.api.libs.json.Json

/**
 * JsonImplicits
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
trait JsonImplicits {
  implicit val __menuItemFormats = Json.format[MenuItem]
  implicit val __menuFormats = Json.format[Menu]
}

object JsonImplicits extends JsonImplicits
