package me.yangbajing.weixin.mp.api

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ResponseEntity
import akka.pattern.ask
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.weixin.mp.domain._
import me.yangbajing.weixin.mp.message.MessageType
import me.yangbajing.weixin.mp.response.ErrMsg
import me.yangbajing.weixin.mp.setting.{SettingAccount, WeixinSetting}
import me.yangbajing.weixin.mp.JsonImplicits._
import me.yangbajing.weixin.mp.WeixinUnmarshallers._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object WeixinClient {
  def apply(account: SettingAccount,
            setting: WeixinSetting,
            initFunc: () => (Future[Option[AccessToken]], Future[Option[JsapiTicket]]),
            refreshFunc: (Option[AccessToken], Option[JsapiTicket]) => Unit)(implicit system: ActorSystem, materializer: ActorMaterializer): WeixinClient = {
    new WeixinClient(account, setting, initFunc, refreshFunc)
  }
}

case object GetAccessToken

case class SetAccessToken(accessToken: AccessToken)

class AccessTokenActor(api: HttpApi,
                       refreshFunc: (Option[AccessToken], Option[JsapiTicket]) => Unit) extends Actor {

  import context.dispatcher

  @volatile private var _accessToken: Either[ErrMsg, AccessToken] = Right(AccessToken("", 0))

  override def receive: Receive = {
    case GetAccessToken =>
      if (_accessToken.right.exists(_.isValid))
        sender() ! _accessToken
      else {
        val f = api.getAccessToken
        val result = Await.result(f, api.setting.akkaTimeout.duration)
        if (result.isRight) {
          _accessToken = result
          context.system.scheduler.scheduleOnce(Duration(1, TimeUnit.SECONDS))(refreshFunc(result.right.toOption, None))
        }
        sender() ! result
      }

    case SetAccessToken(accessToken) =>
      _accessToken = Right(accessToken)
  }
}

case object GetJsapiTicket

case class SetJsapiTicket(jsapiTicket: JsapiTicket)

class JsapiTicketActor(api: HttpApi,
                       accessTokenActor: ActorRef,
                       refreshFunc: (Option[AccessToken], Option[JsapiTicket]) => Unit) extends Actor {

  import api.setting.akkaTimeout
  import context.dispatcher

  @volatile private var _jsapiTicket: Either[ErrMsg, JsapiTicket] = Right(JsapiTicket("", 0))

  override def receive: Actor.Receive = {
    case GetJsapiTicket =>
      if (_jsapiTicket.right.exists(_.isValid))
        sender() ! _jsapiTicket
      else {
        val f = (accessTokenActor ? GetAccessToken).mapTo[Either[ErrMsg, AccessToken]].flatMap {
          case Right(accessToken) => api.ticketGetticket(accessToken)
          case Left(err) => Future.successful(Left(err))
        }

        val result = Await.result(f, api.setting.akkaTimeout.duration)
        if (result.isRight) {
          _jsapiTicket = result
          context.system.scheduler.scheduleOnce(Duration(1, TimeUnit.SECONDS))(refreshFunc(None, result.right.toOption))
        }
        sender() ! result
      }

    case SetJsapiTicket(jsapiTicket) =>
      _jsapiTicket = Right(jsapiTicket)
  }
}

class WeixinClient private(account: SettingAccount,
                           setting: WeixinSetting,
                           initFunc: () => (Future[Option[AccessToken]], Future[Option[JsapiTicket]]),
                           refreshFunc: (Option[AccessToken], Option[JsapiTicket]) => Unit)(implicit system: ActorSystem, materializer: ActorMaterializer)
  extends StrictLogging {

  import setting.akkaTimeout
  import system.dispatcher

  private val api = HttpApi(account, setting)

  val accessTokenActor = system.actorOf(Props(new AccessTokenActor(api, refreshFunc)))
  val jsapiTicketActor = system.actorOf(Props(new JsapiTicketActor(api, accessTokenActor, refreshFunc)))

  initFunc() match {
    case (f1, f2) =>
      f1.onSuccess {
        case Some(at) => accessTokenActor ! SetAccessToken(at)
      }
      f2.onSuccess {
        case Some(jt) => jsapiTicketActor ! SetJsapiTicket(jt)
      }
  }

  def getAccessToken = (accessTokenActor ? GetAccessToken).mapTo[Either[ErrMsg, AccessToken]]

  def ticketGetticket = (jsapiTicketActor ? GetJsapiTicket).mapTo[Either[ErrMsg, JsapiTicket]]

  def getcallbackip: Future[Either[ErrMsg, Seq[String]]] =
    getAccessToken.flatMap {
      case Right(at) => api.getcallbackip(at)
      case Left(err) => Future.successful(Left(err))
    }

  def mediaGet(mediaId: String): Future[Either[ErrMsg, ResponseEntity]] =
    getAccessToken.flatMap {
      case Right(at) => api.mediaGet(at, mediaId)
      case Left(err) => Future.successful(Left(err))
    }

  def mediaGetForVideo(mediaId: String): Future[Either[ErrMsg, ResponseEntity]] =
    getAccessToken.flatMap {
      case Right(at) => api.mediaGetToVideo(at, mediaId)
      case Left(err) => Future.successful(Left(err))
    }

  def materialGetMaterialForNews(mediaId: String): Future[Either[ErrMsg, Seq[NewsItem]]] =
    getAccessToken.flatMap {
      case Right(at) => api.materialGetMaterialForNews(at, mediaId)
      case Left(err) => Future.successful(Left(err))
    }

  def materialGetMaterialForVideo(mediaId: String): Future[Either[ErrMsg, DownUrlMsg]] =
    getAccessToken.flatMap {
      case Right(at) => api.materialGetMaterialForVideo(at, mediaId)
      case Left(err) => Future.successful(Left(err))
    }

  def materialGetMaterial(mediaId: String): Future[Either[ErrMsg, ResponseEntity]] =
    getAccessToken.flatMap {
      case Right(at) => api.materialGetMaterial(at, mediaId)
      case Left(err) => Future.successful(Left(err))
    }

  def materialGetMaterialcount: Future[Either[ErrMsg, MaterialCount]] =
    getAccessToken.flatMap {
      case Right(at) => api.materialGetMaterialcount(at)
      case Left(err) => Future.successful(Left(err))
    }

  def materialBatchgetMaterial(`type`: MessageType,
                               offset: Int,
                               count: Int): Future[Either[ErrMsg, BatchgetMaterial]] =
    getAccessToken.flatMap {
      case Right(at) => api.materialBatchgetMaterial(at, `type`, offset, count)
      case Left(err) => Future.successful(Left(err))
    }

}
