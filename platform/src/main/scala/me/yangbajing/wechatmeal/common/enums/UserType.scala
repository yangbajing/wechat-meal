package me.yangbajing.wechatmeal.common.enums

import play.api.libs.json._

/**
 * 用户类型
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
object UserType extends Enumeration {
  type UserType = Value

  val User = Value
  val Admin = Value

  implicit val __userTypeFormats = new Format[UserType.UserType] {
    override def writes(o: UserType): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[UserType] = JsSuccess(UserType.withName(json.as[String]))
  }
}
