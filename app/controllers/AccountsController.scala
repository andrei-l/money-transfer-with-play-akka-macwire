package controllers

import play.api.mvc.{AbstractController, ControllerComponents, Results}

class AccountsController(controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {

  def openAccount() = Action {
    Results.NotImplemented
  }

  def depositMoney(id: Long) = Action {
    Results.NotImplemented
  }

  def transferMoney(id: Long) = Action {
    Results.NotImplemented
  }

  def loadAccount(id: Long) = Action {
    Results.NotImplemented
  }
}
