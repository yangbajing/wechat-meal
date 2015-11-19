package me.yangbajing.wechatmeal.data.repo

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.common.Permissions
import me.yangbajing.wechatmeal.common.enums.UserStatus
import me.yangbajing.wechatmeal.data.driver.MyDriver.api._
import me.yangbajing.wechatmeal.data.model.{Credential, User}
import me.yangbajing.wechatmeal.utils.Utils

import scala.concurrent.{ExecutionContext, Future}

/**
 * UserRepo
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-20.
 */
@Singleton
class UserRepo @Inject()(schemas: Schemas) {

  import schemas._

  def signup(account: String, password: String)(implicit ec: ExecutionContext): Future[Long] = {
    val now = Utils.now()
    val u = for {
      id <- tCredential returning tCredential.map(_.id) += Credential(0L, Array(), Array(), now)
      _ <- tUser += User(id, None, Some(account), UserStatus.CERTIFIED, List(Permissions.ADMIN), now)
    } yield id
    db.run(u.transactionally)
  }

  def signin(account: String, password: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val q = for {
      u <- tUser.filter(_.email === account)
      c <- tCredential.filter(_.id === u.id)
    } yield (u, c)

    db.run(q.result).map(_.headOption).map {
      case Some((u, c)) =>
        if (Utils.matchPassword(c.salt, c.password, password)) Some(u)
        else throw new RuntimeException("密码不正确")

      case None =>
        None
    }
  }

  def update(userId: Long, body: User)(implicit ec: ExecutionContext) = {
    val action = tUser.filter(_.id === userId).update(body).transactionally
    db.run(action).map(_ => body)
  }

  def findOneById(userId: Long)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(tUser.filter(_.id === userId).result).map(_.headOption)
  }

  def insert(user: User)(implicit ec: ExecutionContext): Future[Long] = {
    val now = Utils.now()
    val u = for {
      id <- tCredential returning tCredential.map(_.id) += Credential(0L, Array(), Array(), now)
      _ <- tUser += user.copy(id = id)
    } yield id
    db.run(u.transactionally)
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
