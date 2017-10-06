package controllers

import play.api.mvc.{AbstractController, ControllerComponents}

class AccountsController(controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {

  def openAccount() = play.mvc.Results.TODO

  def depositMoney(id: Long) = play.mvc.Results.TODO

  def transferMoney(id: Long) = play.mvc.Results.TODO

  def loadAccount(id: Long) = play.mvc.Results.TODO
}
