package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.data.model.Menu
import me.yangbajing.wechatmeal.service.MenuService
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}
import utils.BaseController

/**
 * Menu
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-04.
 */
@Singleton
class MenuController @Inject()(menuService: MenuService) extends Controller with BaseController {

  def create() = Action.async(parse.json.map(_.as[Menu])) { request =>
    menuService.insert(request.body).map { id =>
      Created(Json.obj("id" -> id))
    }
  }

  def findOneById(menuId: Long) = Action.async {
    menuService.findOneById(menuId).map {
      case Some(menu) => Ok(Json.toJson(menu))
      case None => NotFound
    }
  }

}
