package me.yangbajing.wechatmeal.common.enums

import play.api.libs.json._

/**
 * 餐类型
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-19.
 */
object MealType extends Enumeration {
  type MealType = Value

  val Breakfast = Value("B")
  val Lunch = Value("L")
  val Dinner = Value("D")

  implicit val __mealTypeFormats = new Format[MealType] {
    override def writes(o: MealType): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[MealType] = JsSuccess(MealType.withName(json.as[String]))
  }
}
