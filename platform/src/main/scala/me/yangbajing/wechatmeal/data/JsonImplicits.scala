package me.yangbajing.wechatmeal.data

import me.yangbajing.wechatmeal.data.domain.MenuItem
import me.yangbajing.wechatmeal.data.model.{MealHistory, Menu, User}
import org.bson.types.ObjectId
import play.api.libs.json._

/**
 * JsonImplicits
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
trait JsonImplicits {
  implicit val __objectIdFormat = new Format[ObjectId] {
    override def reads(json: JsValue): JsResult[ObjectId] = JsSuccess(new ObjectId(json.as[String]))

    override def writes(o: ObjectId): JsValue = JsString(o.toString)
  }
  implicit val __userFormat = Json.format[User]
  implicit val __menuFormat = Json.format[Menu]
  implicit val __menuItemFormat = Json.format[MenuItem]
  implicit val __mealHistoryFormat = Json.format[MealHistory]
}

object JsonImplicits extends JsonImplicits
