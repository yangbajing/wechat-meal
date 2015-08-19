package me.yangbajing.wechatmeal.service.actors

import akka.actor.{Actor, Props}
import akka.pattern.ask
import me.yangbajing.wechatmeal.data.repo.Schemas
import me.yangbajing.wechatmeal.service.actors.Commands._

/**
 * UserMaster 分发所有指令请求到UserWorker
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserMaster {
  def props(schemas: Schemas) = Props(new UserMaster(schemas))
}

class UserMaster(schemas: Schemas) extends Actor {

  import context.dispatcher
  import me.yangbajing.wechatmeal.utils.Utils.timeout

  override def receive: Receive = {
    case c@Command(openid, command) =>
      val doSender = sender()
      val ref = context.child(openid) getOrElse context.actorOf(UserWorker.props(schemas))
      (ref ? c).onSuccess { case msg => doSender ! msg }
  }
}
