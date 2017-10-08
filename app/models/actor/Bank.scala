package models.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.collection.mutable
import scala.util.{Failure, Success}

class Bank(bankAccountMaker: (ActorRefFactory, String) => ActorRef)
  extends Actor {

  private var accountIdCounter = 0L
  private val accounts = new mutable.LongMap[ActorRef]()

  private final implicit val DefaultTimeout = Timeout(500, TimeUnit.MILLISECONDS)

  import BankSupportedOperations.SupportedResponses._
  import BankSupportedOperations._
  import SupportedOperations.SupportedResponses._
  import SupportedOperations._

  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive: Receive = {
    case OpenAccount(accountName) => openAccount(accountName)
    case TransferMoney(fromId, toId, amount) => transferMoney(fromId, toId, amount)
    case DepositMoney(accountId, amount) => depositMoney(accountId, amount)
    case WithdrawMoney(accountId, amount) => withdrawMoney(accountId, amount)
    case GetAccountDetails(accountId) => getAccountDetails(accountId)
  }

  private def openAccount(accountName: String) = {
    val replyTo = sender()
    val newAccount = bankAccountMaker(context, accountName)
    val accountId = nextAccountId()
    accounts += accountId -> newAccount
    (newAccount ? OpenAccount(accountName)) onComplete {
      case Success(Ok) => replyTo ! AccountCreated(accountId)
      case _ => replyTo ! FailedOperation("Unknown error")
    }
  }

  private def transferMoney(fromId: Long, toId: Long, amount: BigDecimal) = {
    val fromAccount = accounts(fromId)
    val toAccount = accounts(toId)
    val replyTo = sender
    (fromAccount ? BankAccountSupportedOperations.WithdrawMoney(amount)) onComplete {
      case Success(Ok) =>
        (toAccount ? BankAccountSupportedOperations.DepositMoney(amount)) pipeTo replyTo
      case Success(other) => replyTo ! other
      case Failure(ex) => replyTo ! FailedOperation(s"Unknown error: ${ex.getMessage}")
    }
  }

  private def getAccountDetails(accountId: Long) = {
    val account = accounts(accountId)
    (account ? BankAccountSupportedOperations.GetAccountDetails) pipeTo sender()
  }

  private def depositMoney(accountId: Long, amount: BigDecimal) = {
    val account = accounts(accountId)
    (account ? BankAccountSupportedOperations.DepositMoney(amount)) pipeTo sender()
  }

  private def withdrawMoney(accountId: Long, amount: BigDecimal) = {
    val account = accounts(accountId)
    (account ? BankAccountSupportedOperations.WithdrawMoney(amount)) pipeTo sender()
  }

  private def nextAccountId() = {
    val previousAccountId = accountIdCounter
    accountIdCounter += 1
    previousAccountId
  }
}
