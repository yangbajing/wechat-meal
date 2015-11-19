package me.yangbajing.wechatmeal.data.model

import java.time.ZonedDateTime

import me.yangbajing.wechatmeal.common.enums.UserStatus
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._

/**
 * User
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
case class User(id: Long,
                openid: Option[String],
                email: Option[String],
                status: UserStatus.UserStatus,
                permissions: List[String],
                createdAt: ZonedDateTime)

class TableUser(tag: Tag) extends Table[User](tag, "t_user") {
  val id = column[Long]("id", O.PrimaryKey)
  val openid = column[Option[String]]("openid")
  val email = column[Option[String]]("email")
  val status = column[UserStatus.UserStatus]("status")
  val permissions = column[List[String]]("permissions")
  val createdAt = column[ZonedDateTime]("createdAt")

  def __idxOpenid = index(tableName + "_idx_openid", openid, true)

  def __idxEmail = index(tableName + "_idx_email", email, true)

  def * = (id, openid, email, status, permissions, createdAt) <>(User.tupled, User.unapply)
}
