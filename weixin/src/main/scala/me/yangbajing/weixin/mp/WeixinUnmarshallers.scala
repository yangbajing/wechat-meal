package me.yangbajing.weixin.mp

import akka.http.scaladsl.model.{HttpCharsets, MediaTypes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.stream.ActorMaterializer
import play.api.libs.json.{Json, JsValue}

trait WeixinUnmarshallers {
  implicit def __playJsonUnmarshaller(implicit am: ActorMaterializer): FromEntityUnmarshaller[JsValue] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(MediaTypes.`application/json`, MediaTypes.`text/plain`).mapWithCharset { (data, charset) =>
      if (charset == HttpCharsets.`UTF-8`) Json.parse(data.toArray)
      else Json.parse(data.decodeString(charset.nioCharset.name()))
    }
  }
}

object WeixinUnmarshallers extends WeixinUnmarshallers
