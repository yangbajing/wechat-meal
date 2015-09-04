package me.yangbajing.wechatmeal.utils

import scala.util.Try

/**
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
object AsInt {
  def unapply(v: String): Option[Int] = Try(v.toInt).toOption
}
