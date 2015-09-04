package me.yangbajing.wechatmeal.service.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import com.typesafe.scalalogging.{LazyLogging, StrictLogging}
import me.yangbajing.wechatmeal.common.enums.UserStatus
import me.yangbajing.wechatmeal.data.model.User
import me.yangbajing.wechatmeal.data.repo.{UserRepo, Schemas}
import me.yangbajing.wechatmeal.service.actors.command.{SetUser, Command, CommandResult}
import me.yangbajing.wechatmeal.utils.Utils
import play.api.cache.CacheApi

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * UserMaster 分发所有指令请求到UserWorker
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserMaster {
  def props(schemas: Schemas, cacheApi: CacheApi) = Props(new UserMaster(schemas, cacheApi))
}

class UserMaster(schemas: Schemas, cacheApi: CacheApi) extends Actor with LazyLogging {

  import context.dispatcher
  import me.yangbajing.wechatmeal.utils.Utils.timeout

  override def receive: Receive = {
    case command@Command(openid, _) =>
      logger.debug(command.toString)
      val doSender = sender()

      (context.child(openid) match {
        case Some(worker) =>
          worker ? command
        case None =>
          val userRepo = UserRepo(schemas)
          userRepo.findOneByOpenid(openid).flatMap {
            case Some(user) =>
              Future.successful(user)
            case None =>
              val user = User(0L, Some(openid), None, UserStatus.INACTIVE, Utils.now())
              userRepo.insert(user).map(id => user.copy(id = id))
          }.flatMap { initUser =>
            context.actorOf(UserWorker.props(initUser, schemas, cacheApi), openid) ? command
          }
      }) onComplete {
        case Success(result) =>
          doSender ! result
        case Failure(e) =>
          logger.error(e.toString, e)
          doSender ! CommandResult("InternalServerError")
      }

    case sc@SetUser(user) =>
      user.openid.foreach { openid =>
        context.child(openid).foreach(_ ! sc)
      }
  }

}
