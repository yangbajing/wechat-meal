package me.yangbajing.weixin.mp.message

import scala.xml.Node

case class EventMessage(override val node: Node) extends BaseMessage(node) {

  import me.yangbajing.weixin.mp.WeixinTools.text

  val event = EventTypes(text(node, "Event"))
  lazy val eventKey = text(node, "EventKey")
  lazy val ticket = text(node, "Ticket")
  lazy val latitude = text(node, "Latitude")
  lazy val longitude = text(node, "Longitude")
  lazy val precision = text(node, "Precision")
}
