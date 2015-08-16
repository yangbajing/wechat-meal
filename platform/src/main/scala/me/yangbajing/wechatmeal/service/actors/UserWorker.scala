package me.yangbajing.wechatmeal.service.actors

import akka.actor.{Actor, Props}

/**
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserWorker {
  def props = Props(new UserWorker)
}

class UserWorker extends Actor {
  override def receive: Receive = {
    case _ =>
  }
}
