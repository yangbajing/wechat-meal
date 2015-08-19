package me.yangbajing.wechatmeal.service.actors

import java.time.LocalDate

import akka.actor.{Actor, Props}
import me.yangbajing.wechatmeal.data.model.{Menu, User}
import me.yangbajing.wechatmeal.data.repo.{UserRepo, Schemas}
import me.yangbajing.wechatmeal.service.actors.Commands._
import play.api.Play.current
import play.api.cache.Cache

/**
 * UserWorker
 * 用户绑定成功后才会进行 UserWorker
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserWorker {
  def props(schemas: Schemas) = Props(new UserWorker(schemas))
}

class UserWorker(schemas: Schemas) extends Actor {

  import context.dispatcher

  @volatile var user = Option.empty[User]

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    val userRepo = UserRepo(schemas)
    val openid = self.path.name

    // TODO 延时怎么处理？
    userRepo.findOneByOpenid(openid).onSuccess { case u =>
      user = u
      if (user.isEmpty) {
        context.become(bindReceive)
      }
    }
  }

  override def receive: Receive = {
    case Command(openid, Command.MENU) =>
      val nowDate = LocalDate.now()
      Cache.getAs[Menu]("menu-" + nowDate) match {
        case Some(menu) if menu.date == nowDate =>
          menu.menus.zipWithIndex.map { case (item, idx) => s"${idx + 1}: ${item.name} (￥${item.price})" }
          context.become(menuReceive(menu))

        case _ =>
          sender() ! CommandResult("当日菜单未生成")
      }

    case Command(_, Command.HISTORY) =>
      sender() ! CommandResult("点餐历史记录未实现")

    case Command(_, Command.BIND) =>
      sender() ! CommandResult(COMMAND_BIND)
      context.become(bindReceive)

    case Command(_, _) =>
      sender() ! CommandResult(COMMAND_UNBIND_HELP)
  }

  def bindReceive: Receive = {
    case BindEmail(_, email) =>
      // TODO 绑定账号逻辑
      sender() ! CommandResult(s"$email 绑定成功")
      context.become(receive)
  }

  def menuReceive(menu: Menu): Receive = {
    case SelectMeal(openid, mealNo) /*if openid == user.openid*/ =>
      if (mealNo < 1 || mealNo > menu.menus.size) {
        sender() ! s"选项无效，请输入：[1-${
          menu.menus.size
        }}]"
      } else {
        val item = menu.menus(mealNo - 1)
        sender() ! s"已点餐：$item"
      }

    case _ =>
      sender() ! CommandResult(COMMAND_HELP)
      context.become(receive)
  }
}
