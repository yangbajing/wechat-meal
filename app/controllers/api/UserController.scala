package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.model.User
import me.yangbajing.wechatmeal.service.UserService
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.BaseController

/**
 * User API
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-03.
 */
@Singleton
class UserController @Inject()(userService: UserService) extends Controller with BaseController {

  def update(userId: Long) = Action.async(parse.json.map(_.as[User])) { request =>
    userService.update(userId, request.body).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def get(userId: Long) = Action.async {
    userService.findOneById(userId).map {
      case Some(user) =>
        Ok(Json.toJson(user))
      case None =>
        NotFound
    }
  }

  def signin = Action.async(parse.json) { request =>
    val account = request.body.\("account").as[String]
    val password = request.body.\("password").as[String]
    userService.signin(account, password).map {
      case Some(user) =>
        Ok(Json.toJson(user))
      case None =>
        NotFound
    }
  }

  def signup = Action.async(parse.json) { request =>
    val account = request.body.\("account").as[String]
    val password = request.body.\("password").as[String]
    userService.signup(account, password).map { id =>
      Ok(Json.obj("id" -> id))
    }
  }

}
