package controllers.api

import javax.inject.{Inject, Singleton}

import me.yangbajing.wechatmeal.service.MealHistoryService
import me.yangbajing.wechatmeal.utils.Utils
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.BaseController

/**
 * Meals
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-09-04.
 */
@Singleton
class MealHistoryController @Inject()(mealHistoryService: MealHistoryService) extends Controller with BaseController {

  def currentDate() = Action.async {
    mealHistoryService.findByDate(Utils.nowDate()).map { list =>
      Ok(Json.toJson(list))
    }
  }
}
