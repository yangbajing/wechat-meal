package me.yangbajing.weixin.mp.message

sealed abstract class EventType(val name: String) {
  override def toString: String = name

  def isLocation = this == EventTypes.Location

  def isSubscribe = this == EventTypes.Subscribe

  def isUnsubscribe = this == EventTypes.Snsubscribe

  def isClick = this == EventTypes.Click

  def isView = this == EventTypes.View

  def isScan = this == EventTypes.Scan
}

object EventTypes {

  case object Subscribe extends EventType("subscribe")

  case object Snsubscribe extends EventType("unsubscribe")

  case object Scan extends EventType("SCAN")

  case object Location extends EventType("location")

  case object Click extends EventType("CLICK")

  case object View extends EventType("VIEW")

  def apply(v: String): EventType = v match {
    case Click.name => Click
    case View.name => View
    case Location.name => Location
    case Subscribe.name => Subscribe
    case Scan.name => Scan
    case Snsubscribe.name => Snsubscribe
  }
}
