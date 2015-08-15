package me.yangbajing.weixin.mp

import com.typesafe.scalalogging.StrictLogging

import scala.xml.{Node, Utility}

object WeixinTools extends StrictLogging {
  final val CHARSET = "UTF-8"

  @inline
  def text(node: Node, label: String): String = (node \ label).text

  def stringify(node: Node, stripComments: Boolean = true): String =
    Utility.serialize(Utility.trim(node), stripComments = true).toString()
}
