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

  def update(userId: Long, body: User)(implicit ec: ExecutionContext) = {
    val action = tUser.filter(_.id === userId).update(body).transactionally
    db.run(action).map(_ => body)
  }

  def findOneById(userId: Long)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(tUser.filter(_.id === userId).result).map(_.headOption)
  }

  def insert(user: User): Future[Long] = {
    val action = (tUser returning tUser.map(_.id) += user).transactionally
    db.run(action)
  }

  def findOneByOpenid(openid: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(tUser.filter(_.openid === openid).result).map(_.headOption)
  }

  def updateEmailByOpenid(openid: String, email: String): Future[Int] = {
    val action = tUser.filter(_.openid === openid).map(_.email).update(Some(email)).transactionally
    db.run(action)
  }
}

object UserRepo {
  def apply(schemas: Schemas) = new UserRepo(schemas)
}
