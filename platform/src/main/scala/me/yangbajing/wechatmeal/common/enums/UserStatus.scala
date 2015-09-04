package me.yangbajing.wechatmeal.common.enums

import play.api.libs.json._

/**
 * 用户状态
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
object UserStatus extends Enumeration {
  type UserStatus = Value
  val INACTIVE = Value
  val ACTIVE = Value

  implicit val __format = new Format[UserStatus.UserStatus] {
    override def writes(o: UserStatus): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[UserStatus] = JsSuccess(UserStatus.withName(json.as[String]))
  }
}
