package me.yangbajing.wechatmeal.service.actors

import akka.actor.{Actor, Props}

/**
 * UserMaster 分发所有指令请求到UserWorker
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserMaster {
  def props = Props(new UserMaster)
}

class UserMaster extends Actor {
  override def receive: Receive = {
    case _ =>
  }
}
