package me.yangbajing.wechatmeal.data.model

import java.time.LocalDateTime

/**
 * User
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
case class User(id: Long,
                openid: Option[String],
                email: String,
                createdAt: LocalDateTime) {

}
