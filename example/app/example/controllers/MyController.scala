package example.controllers

import example.views.{html => views}
import play.api.mvc._

object MyController extends Controller {

  def index = Action { request =>
    Ok(views.index())
  }

}
