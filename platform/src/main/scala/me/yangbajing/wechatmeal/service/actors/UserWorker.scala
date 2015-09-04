package me.yangbajing.wechatmeal.service.actors

import java.time.ZonedDateTime

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.StrictLogging
import me.yangbajing.wechatmeal.common.enums.UserStatus
import me.yangbajing.wechatmeal.data.model.{MealHistory, Menu, User}
import me.yangbajing.wechatmeal.data.repo.{MealHistoryRepo, Schemas, UserRepo}
import me.yangbajing.wechatmeal.service.actors.command.CommandConstants._
import me.yangbajing.wechatmeal.service.actors.command.{Command, CommandResult, SetUser}
import me.yangbajing.wechatmeal.utils.{AsInt, Utils}
import org.bson.types.ObjectId
import play.api.cache.CacheApi

import scala.util.{Failure, Success}

/**
 * 每微信会话以openid为name创建一个actor
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object UserWorker {
  def props(initUser: Option[User], schemas: Schemas, cacheApi: CacheApi) = Props(new UserWorker(initUser, schemas, cacheApi))
}

class UserWorker(initUser: Option[User], schemas: Schemas, cacheApi: CacheApi) extends Actor with StrictLogging {

  import context.dispatcher

  @volatile var curUser = initUser

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    logger.info("preStart() curUser: " + curUser)
    //    curUser match {
    //      case None =>
    //        context.b
    //      case _ =>
    // do nothing
    //    }
  }

  override def receive: Receive = {
    case command: Command =>
      curUser match {
        case Some(user) if user.status == UserStatus.ACTIVE =>
          // if user active
          // agent to commandReceive
          commandReceive(command)
        case Some(user) =>
          // if user inactive
          // agent to pendingActiveReceive
          pendingActiveReceive(command)
        case None =>
          sender() ! CommandResult(COMMAND_BIND)
          context.become(bindReceive)
      }

    case SetUser(user) =>
      curUser = Some(user)
      //      if (user.status == UserStatus.ACTIVE) {
      context.become(receive)
    //      }
  }

  val commandReceive: PartialFunction[Command, Unit] = {
    case Command(openid, Command.MENU) =>
      val nowDate = Utils.nowDate()
      val key = "menu-" + nowDate
      logger.info("get cache key: " + key)
      cacheApi.get[Menu](key) match {
        case Some(menu) if menu.date isEqual nowDate =>
          logger.info(menu.toString)
          logger.info((menu.date isEqual nowDate).toString)
          menu.menus.zipWithIndex.map { case (item, idx) => s"${idx + 1}: ${item.name} (￥${item.price})" }
          sender() ! CommandResult(menu.prettyString())
          context.become(menuReceive(menu))
        case _ =>
          sender() ! CommandResult("当日菜单未生成\n\n" + COMMAND_HELP)
      }

    case Command(_, Command.HISTORY) =>
      MealHistoryRepo(schemas).findByDate(Utils.nowDate())

      sender() ! CommandResult("点餐历史记录未实现\n\n" + COMMAND_HELP)

    case _ =>
      sender() ! CommandResult(COMMAND_HELP)
  }

  val pendingActiveReceive: Receive = {
    case c@Command(_, Command.BIND) =>
      sender() ! CommandResult(COMMAND_BIND)
      context.become(bindReceive)

    case Command(_, _) =>
      curUser match {
        case Some(u) =>
          sender() ! CommandResult(s"已绑定账号：${u.email}，请等待管理员审核。或输入：\n1: 重新绑定公司邮箱")
        case None =>
          sender() ! CommandResult("internal server error\n\n" + COMMAND_HELP)
      }
  }

  def bindReceive: Receive = {
    case Command(openid, email) =>
      // TODO 校验邮箱格式

      val doSender = sender()
      UserRepo(schemas).updateEmailByOpenid(openid, email).onComplete {
        case Success(_) =>
          doSender ! CommandResult(s"账号：$email 绑定成功\n\n" + COMMAND_HELP)
        case Failure(e) =>
          logger.error(e.toString, e)
          doSender ! CommandResult(s"账号：$email 已被绑定，请使用其它邮箱进行绑定")
      }
  }

  def menuReceive(menu: Menu): Receive = {
    case Command(openid, AsInt(mealNo)) /*if openid == user.openid*/ =>
      if (mealNo < 1 || mealNo > menu.menus.size) {
        sender() ! CommandResult(menu.prettyString() + s"\n\n选项无效，请输入：[1-${menu.menus.size}}]")
      } else {
        val item = menu.menus(mealNo - 1)

        // 持久化到数据库
        val mealHistory = MealHistory(ObjectId.get(), curUser.get.id, item.name, item.price, Utils.nowDate(), Utils.now())
        MealHistoryRepo(schemas).insert(mealHistory)

        sender() ! CommandResult(s"已点餐：${item.name} ￥${item.price}\n\n" + COMMAND_HELP)
      }

    case _ =>
      sender() ! CommandResult(COMMAND_HELP)
      context.become(receive)
  }

}
