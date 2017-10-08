package controllers

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import models.exchange._
import play.api.libs.json.Json.toJson
import play.api.mvc.{AbstractController, ControllerComponents}


class AccountsController(controllerComponents: ControllerComponents,
                         bank: ActorRef) extends AbstractController(controllerComponents) {
  private final implicit val DefaultTimeout = Timeout(1, TimeUnit.SECONDS)

  import models.actor.BankSupportedOperations.SupportedResponses._
  import models.actor.BankSupportedOperations._
  import models.actor.SupportedOperations.{SupportedResponses, _}

  import scala.concurrent.ExecutionContext.Implicits.global


  def openAccount() = Action.async(parse.json[OpenAccountRequest]) { request =>
    val openAccountRequest = request.body

    (bank ? OpenAccount(openAccountRequest.accountName)) map {
      case AccountCreated(accountId) => Created(toJson(AccountIdResponse(accountId)))
      case SupportedResponses.FailedOperation(msg) => BadRequest(toJson(OperationFailedResponse(msg)))
    }
  }

  def depositMoney(id: Long) = Action.async(parse.json[DepositMoneyRequest]) { request =>
    val depositMoneyRequest = request.body

    (bank ? DepositMoney(id, depositMoneyRequest.amount)) map {
      case SupportedResponses.Ok => Ok
      case SupportedResponses.FailedOperation(msg) => BadRequest(toJson(OperationFailedResponse(msg)))
    }
  }

  def withdrawMoney(id: Long) = Action.async(parse.json[WithdrawMoneyRequest]) { request =>
    val withdrawMoneyRequest = request.body

    (bank ? WithdrawMoney(id, withdrawMoneyRequest.amount)) map {
      case SupportedResponses.Ok => Ok
      case SupportedResponses.FailedOperation(msg) => BadRequest(toJson(OperationFailedResponse(msg)))
    }
  }

  def transferMoney(id: Long) = Action.async(parse.json[TransferMoneyRequest]) { request =>
    val transferMoneyRequest = request.body

    (bank ? TransferMoney(id, transferMoneyRequest.destinationAccountId, transferMoneyRequest.amount)) map {
      case SupportedResponses.Ok => Ok
      case SupportedResponses.FailedOperation(msg) => BadRequest(toJson(OperationFailedResponse(msg)))
    }
  }

  def loadAccount(id: Long) = Action.async {
    (bank ? GetAccountDetails(id)) map {
      case SupportedResponses.BankAccountDetails(accName, balance) => Ok(toJson(AccountInfoResponse(accName, balance)))
      case SupportedResponses.FailedOperation(msg) => BadRequest(toJson(OperationFailedResponse(msg)))
    }
  }
}
