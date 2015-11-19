package controllers

import javax.inject.Singleton

import play.api.mvc.{Action, Controller}

/**
 * Page Controller
 * Created by Yang Jing (yangbajing@gmail.com) on 2015-08-15.
 */
@Singleton
class PageController extends Controller {
  def index = Action {
    Ok(views.html.index())
  }
}
