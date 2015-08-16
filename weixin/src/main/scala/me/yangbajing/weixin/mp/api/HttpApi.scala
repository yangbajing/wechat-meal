package me.yangbajing.weixin.mp.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.weixin.mp.JsonImplicits._
import me.yangbajing.weixin.mp.WeixinUnmarshallers._
import me.yangbajing.weixin.mp.domain._
import me.yangbajing.weixin.mp.message.MessageType
import me.yangbajing.weixin.mp.response.ErrMsg
import me.yangbajing.weixin.mp.setting.{SettingAccount, WeixinSetting}
import play.api.libs.json.JsValue

import scala.concurrent.Future

object HttpApi {
  def apply(account: SettingAccount, setting: WeixinSetting
             )(implicit system: ActorSystem, materializer: ActorMaterializer): HttpApi =
    new HttpApi(account, setting)
}

/**
 * 产品推荐使用 WeixinClient
 * @param account
 * @param setting
 * @param system
 * @param materializer
 */
class HttpApi private(val account: SettingAccount, val setting: WeixinSetting
                       )(implicit system: ActorSystem, materializer: ActorMaterializer)
  extends StrictLogging {

  import system.dispatcher

  def getAccessToken: Future[Either[ErrMsg, AccessToken]] = {
    val url = setting.api.accessToken.format(account.appId, account.appSecret)
    httpJson(HttpRequest(uri = url)).map(_.right.map(_.as[AccessToken]))
  }

  def ticketGetticket(accessToken: AccessToken): Future[Either[ErrMsg, JsapiTicket]] = {
    val url = setting.api.ticketGetticket.format(accessToken.access_token)
    httpJson(HttpRequest(uri = url)).map { result =>
      println("ticketGetticket: " + result)
      result.right.map(_.as[JsapiTicket])
    }
  }

  /**
   * 获取微信服务器IP地址
   * @param accessToken
   * @return
   */
  def getcallbackip(accessToken: AccessToken): Future[Either[ErrMsg, Seq[String]]] = {
    val url = setting.api.getcallbackip.format(accessToken.access_token)
    httpJson(HttpRequest(uri = url)).map(_.right.map(_.\("ip_list").as[Seq[String]]))
  }

  def materialGetMaterial(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, ResponseEntity]] = {
    val url = setting.api.materialGetMaterial.format(accessToken.access_token)
    val request = HttpRequest(HttpMethods.POST, url,
      entity = HttpEntity(MediaTypes.`application/json`, s"""{"media_id":"$mediaId"}"""))
    http(request)
  }

  /**
   * 获取永久图文消息素材
   * @param accessToken
   * @param mediaId 多媒体ID
   */
  def materialGetMaterialForNews(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, Seq[NewsItem]]] = {
    _materialGetMaterialToJson(accessToken, mediaId).map(_.right.map(_.\("news_item").as[Seq[NewsItem]]))
  }

  /**
   * 获取永久视频消息素材
   * @param accessToken
   * @param mediaId 多媒体ID
   */
  def materialGetMaterialForVideo(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, DownUrlMsg]] = {
    _materialGetMaterialToJson(accessToken, mediaId).map(_.right.map(_.as[DownUrlMsg]))
  }

  /**
   * 获取永久素材
   * @param accessToken
   * @param mediaId 多媒体ID
   */
  private def _materialGetMaterialToJson(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, JsValue]] = {
    val url = setting.api.materialGetMaterial.format(accessToken.access_token)
    val request = HttpRequest(HttpMethods.POST, url,
      entity = HttpEntity(MediaTypes.`application/json`, s"""{"media_id":"$mediaId"}"""))
    httpJson(request)
  }

  /**
   * 获取素材总数
   * @param accessToken
   * @return
   */
  def materialGetMaterialcount(accessToken: AccessToken): Future[Either[ErrMsg, MaterialCount]] = {
    val url = setting.api.materialGetMaterialcount.format(accessToken.access_token)
    httpJson(HttpRequest(uri = url)).map(_.right.map(_.as[MaterialCount]))
  }

  /**
   * 获取素材列表
   * 1、获取永久素材的列表，也会包含公众号在公众平台官网素材管理模块中新建的图文消息、语音、视频等素材（但需要先通过获取素材列表来获知素材的media_id）
   * 2、临时素材无法通过本接口获取
   * 3、调用该接口需https协议
   * @param accessToken
   * @param `type` MessageType 素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）
   * @param offset 从全部素材的该偏移位置开始返回，0表示从第一个素材 返回
   * @param count 返回素材的数量，取值在1到20之间
   * @return
   */
  def materialBatchgetMaterial(accessToken: AccessToken,
                               `type`: MessageType,
                               offset: Int,
                               count: Int): Future[Either[ErrMsg, BatchgetMaterial]] = {
    val url = setting.api.materialPatchgetMaterial.format(accessToken.access_token)
    val data = s"""{"type":"${`type`.name}","offset":$offset,"count":$count}"""
    val request = HttpRequest(HttpMethods.POST, url, entity = HttpEntity(MediaTypes.`application/json`, data))
    httpJson(request).map(_.right.map(_.as[BatchgetMaterial]))
  }

  /**
   * 获取临时素材（视频）
   * @param accessToken
   * @param mediaId 多媒体ID
   * @return
   */
  def mediaGetToVideo(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, ResponseEntity]] = {
    val url = "http" + setting.api.mediaGet.format(accessToken.access_token, mediaId).drop(5)
    http(HttpRequest(uri = url))
  }

  /**
   * 获取临时素材
   * @param accessToken
   * @param mediaId 多媒体ID
   * @return
   */
  def mediaGet(accessToken: AccessToken, mediaId: String): Future[Either[ErrMsg, ResponseEntity]] = {
    val url = setting.api.mediaGet.format(accessToken.access_token, mediaId)
    http(HttpRequest(uri = url))
  }

  protected def httpJson(request: HttpRequest): Future[Either[ErrMsg, JsValue]] = {
    logger.debug(request.toString)
    Http().singleRequest(request).flatMap { resp =>
      logger.debug(resp.toString)

      Unmarshal(resp.entity).to[JsValue].map { json =>
        val errcode = (json \ "errcode").asOpt[Int]
        if (errcode.isDefined && errcode.get != 0) {
          Left(json.as[ErrMsg])
        } else {
          Right(json)
        }
      }
    }
  }

  protected def http(request: HttpRequest): Future[Either[ErrMsg, ResponseEntity]] = {
    logger.debug(request.toString)

    Http().singleRequest(request).flatMap { resp =>
      logger.debug(resp.toString)

      val mediaType = resp.entity.contentType().mediaType
      if (mediaType == MediaTypes.`application/json` || mediaType == MediaTypes.`text/plain`) {
        Unmarshal(resp.entity).to[JsValue].map(json => Left(json.as[ErrMsg]))
      } else {
        Future.successful(Right(resp.entity))
      }
    }
  }
}
