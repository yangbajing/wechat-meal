package me.yangbajing.wechatmeal.data.repo

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.User

import scala.concurrent.{ExecutionContext, Future}

/**
 * UserRepo
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-20.
 */
@Singleton
class UserRepo @Inject()(schemas: Schemas) {

  import schemas._

  def findOneByOpenid(openid: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(tUser.filter(_.openid === openid).result).map(_.headOption)
  }
}

object UserRepo {
  def apply(schemas: Schemas) = new UserRepo(schemas)
}
