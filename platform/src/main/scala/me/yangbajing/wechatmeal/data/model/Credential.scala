package me.yangbajing.wechatmeal.data.model

import java.time.ZonedDateTime
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
/**
 * 密码
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-05.
 */
case class Credential(id: Long,
                      salt: Array[Byte],
                      password: Array[Byte],
                      createdAt: ZonedDateTime)

class TableCredential(tag: Tag) extends Table[Credential](tag, "t_credential") {
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val salt = column[Array[Byte]]("salt")
  val password = column[Array[Byte]]("password")
  val createdAt = column[ZonedDateTime]("createdAt")
  def * = (id, salt, password, createdAt)<>(Credential.tupled, Credential.unapply)
}
