package me.yangbajing.weixin.mp

import me.yangbajing.weixin.mp.domain._
import me.yangbajing.weixin.mp.response.ErrMsg
import play.api.libs.json._

trait JsonImplicits {
  implicit val __errMsgFormats: Format[ErrMsg] = Json.format[ErrMsg]
  implicit val __accessTokenFormats: Format[AccessToken] = new Format[AccessToken] {
    override def writes(o: AccessToken): JsValue =
      Json.obj("access_token" -> o.access_token, "expires_in" -> o.expires_in)

    override def reads(json: JsValue): JsResult[AccessToken] =
      JsSuccess(AccessToken(json.\("access_token").as[String], json.\("expires_in").as[Long]))
  }
  implicit val __jsapiTicketFormats: Format[JsapiTicket] = new Format[JsapiTicket] {
    override def writes(o: JsapiTicket): JsValue =
      Json.obj("ticket" -> o.ticket, "expires_in" -> o.expires_in)

    override def reads(json: JsValue): JsResult[JsapiTicket] =
      JsSuccess(JsapiTicket(json.\("ticket").as[String], json.\("expires_in").as[Long]))
  }
  implicit val __materialCountFormats: Format[MaterialCount] = Json.format[MaterialCount]
  implicit val __downUrlMsgFormats: Format[DownUrlMsg] = Json.format[DownUrlMsg]
  implicit val __newsItemFormats: Format[NewsItem] = Json.format[NewsItem]
  implicit val __materialContentFormats: Format[MaterialContent] = Json.format[MaterialContent]
  implicit val __materialItemFormats: Format[MaterialItem] = Json.format[MaterialItem]
  implicit val __batchgetMaterialFormats: Format[BatchgetMaterial] = Json.format[BatchgetMaterial]
}

object JsonImplicits extends JsonImplicits
