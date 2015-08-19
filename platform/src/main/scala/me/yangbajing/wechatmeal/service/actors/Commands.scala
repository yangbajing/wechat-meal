package me.yangbajing.wechatmeal.service.actors

/**
 * 命令相关的actor
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object Commands {

  val COMMAND_UNBIND_HELP =
    """?: 返回命令菜单
      |1: 绑定账号
      |2: 今日菜单
      |3: 点菜记录""".stripMargin

  val COMMAND_HELP =
    """?: 返回命令菜单
      |2: 今日菜单
      |3: 点菜记录""".stripMargin

  val COMMAND_BIND = "请输入公司邮箱完成账号绑定"

  case class Command(openid: String, command: String)

  object Command {
    val BIND = "1"
    val MENU = "2"
    val HISTORY = "3"
  }

  case class CommandResult(content: String)

  case class BindEmail(openid: String, email: String)

  // 已选择菜品
  case class SelectMeal(openid: String, mealNo: Int)

}
