package me.yangbajing.wechatmeal.data.model

import java.time.{LocalDate, ZonedDateTime}

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import org.bson.types.ObjectId

/**
 * 订餐记录
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-02.
 */
case class MealHistory(id: ObjectId,
                       userId: Long,
                       name: String,
                       price: BigDecimal,
                       date: LocalDate,
                       createdAt: ZonedDateTime)

class TableMealHistory(tag: Tag) extends Table[MealHistory](tag, "t_meal_history") {
  val id = column[ObjectId]("id", O.PrimaryKey, O.SqlType("char(24)"))
  val userId = column[Long]("userId")
  val name = column[String]("name")
  val price = column[BigDecimal]("price")
  val date = column[LocalDate]("date")
  val createdAt = column[ZonedDateTime]("createdAt")

  val __fkUser = foreignKey(tableName + "_fk_user", userId, TableQuery[TableUser])(_.id)

  def * = (id, userId, name, price, date, createdAt) <>(MealHistory.tupled, MealHistory.unapply)
}
