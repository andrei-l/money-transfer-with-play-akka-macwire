package controllers

import akka.actor.ActorRef
import models.exchange._
import play.api.libs.json.Json.toJson
import play.api.mvc.{AbstractController, ControllerComponents}

class AccountsController(controllerComponents: ControllerComponents,
                         bank: ActorRef) extends AbstractController(controllerComponents) {

  def openAccount() = Action(parse.json[OpenAccountRequest]) { request =>
    val openAccountRequest = request.body

    Created(toJson(AccountIdResponse(1)))
  }

  def depositMoney(id: Long) = Action(parse.json[DepositMoneyRequest]) { request =>
    val depositMoneyRequest = request.body

    Ok
  }

  def transferMoney(id: Long) = Action(parse.json[TransferMoneyRequest]) { request =>
    val transferMoneyRequest = request.body

    Ok
  }

  def loadAccount(id: Long) = Action {
    Ok(toJson(AccountInfoResponse("", 0)))
  }
}
