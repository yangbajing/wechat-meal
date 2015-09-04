package me.yangbajing.wechatmeal.data.driver

import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import com.github.tminglei.slickpg.{PgPlayJsonSupport, ExPostgresDriver, PgArraySupport, PgDate2Support}
import me.yangbajing.wechatmeal.common.enums.{UserStatus, MealType}
import org.bson.types.ObjectId
import play.api.libs.json.{Json, JsValue}

/**
 * My Postgres Driver
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-16.
 */
object MyDriver
  extends ExPostgresDriver
  with PgDate2Support
  with PgPlayJsonSupport
  with PgArraySupport {
  override val pgjson = "jsonb"
  override val api = MyAPI

  object MyAPI
    extends API
    with DateTimeImplicits
    with JsonImplicits
    with ArrayImplicits {

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val json4sJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        (v) => SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)

    // custom mapped column
    implicit val __objectIdTypeColumn = MappedColumnType.base[ObjectId, String](_.toString, new ObjectId(_))
    implicit val __mealTypeColumn = MappedColumnType.base[MealType.MealType, String](_.toString, MealType.withName)
    implicit val __userStatusTypeColumn =
      MappedColumnType.base[UserStatus.UserStatus, String](_.toString, UserStatus.withName)
  }

}
