package me.yangbajing.weixin.mp.message

import me.yangbajing.weixin.mp.WeixinTools.text

import scala.xml.{Node, XML}

abstract class BaseMessage(val node: Node) {
  val toUserName = text(node, "ToUserName")
  val fromUserName = text(node, "FromUserName")
  val createTime = text(node, "CreateTime")
  val msgType = OrdinaryMessage.msgType(node)
}

case class OrdinaryMessage(override val node: Node) extends BaseMessage(node) {
  lazy val msgId = text(node, "MsgId").toLong
  lazy val content = text(node, "Content")

  def contentOption = if (msgType.isText) Some(content) else None

  lazy val mediaId = text(node, "MediaId")
  lazy val picUrl = text(node, "PicUrl")
  lazy val format = text(node, "Format")
  lazy val thumbMediaId = text(node, "ThumbMediaId")
  lazy val recongnition = text(node, "Recongnition")
  lazy val locationX = text(node, "Location_X")
  lazy val locationY = text(node, "Location_Y")
  lazy val scale = text(node, "Scale").toInt
  lazy val label = text(node, "Label")
  lazy val title = text(node, "Title")
  lazy val description = text(node, "Description")
  lazy val url = text(node, "Url")
}

object OrdinaryMessage {
  def msgType(node: Node) = MessageTypes(text(node, "MsgType"))

  def apply(v: String) = new OrdinaryMessage(XML.loadString(v))
}
