package me.yangbajing.wechatmeal.service.actors.command

/**
 * 指令（微信文本消息输入）
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
object CommandConstants {

  val COMMAND_HELP =
    """0: 返回命令菜单
      |2: 今日菜单
      |3: 点菜记录""".stripMargin

  val COMMAND_BIND = "请输入公司邮箱完成账号绑定"

}

case class CommandResult(content: String)

case class Command(openid: String, command: String)

object Command {
  val BIND = "1"
  val MENU = "2"
  val HISTORY = "3"
}
