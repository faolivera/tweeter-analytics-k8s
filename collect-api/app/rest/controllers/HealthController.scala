package rest.controllers

import play.api.mvc.InjectedController

class HealthController extends InjectedController {

  def health = Action {
    Ok
  }

}
