package me.yangbajing.wechatmeal.service

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.model.User
import me.yangbajing.wechatmeal.data.repo.UserRepo
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * User Service
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-03.
 */
@Singleton
class UserService @Inject()(userRepo: UserRepo) {
  def update(userId: Long, body: User) = {
    userRepo.update(userId, body).map { bo =>
      // TODO 更新 UserWorker curUser
      bo
    }
  }

  def findOneById(userId: Long) = userRepo.findOneById(userId)

}
