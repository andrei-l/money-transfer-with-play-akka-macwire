package models.actor

import akka.actor.{Actor, ActorRef, ActorRefFactory}

import scala.collection.mutable

class Bank(bankAccountMaker: (ActorRefFactory, String) => ActorRef)
  extends Actor {

  private var accountIdCounter = 0L
  private val accounts = new mutable.LongMap[ActorRef]()

  private val awaitingOperationsFromWithPostOpOnSuccess =
    new mutable.HashMap[ActorRef, mutable.Queue[SuccessAwaitingOperation]]().withDefaultValue(new mutable.Queue())

  import BankSupportedOperations._
  import SupportedOperations._

  override def receive: Receive = {
    case OpenAccount(accountName) => openAccount(accountName)
    case TransferMoney(fromId, toId, amount) => transferMoney(fromId, toId, amount)
    case GetAccountDetails(accountId) => getAccountDetails(accountId)
    case DepositMoney(accountId, amount) => depositMoney(accountId, amount)
    case WithdrawMoney(accountId, amount) => withdrawMoney(accountId, amount)

    case BankAccountSupportedOperations.Success => receivedSucceededOperation()
    case BankAccountSupportedOperations.Failure => receivedFailedOperation()
    case BankAccountSupportedOperations.BankAccountDetails(accountName, accountBalance) =>
      receivedBankAccountDetails(accountName, accountBalance)
  }

  private def openAccount(accountName: String) = {
    val newAccount = bankAccountMaker(context, accountName)
    val accountId = nextAccountId()
    accounts += accountId -> newAccount
    awaitingOperationsFromWithPostOpOnSuccess(newAccount) += newSuccessAwaitingOperation(accountId, sender)
    newAccount ! OpenAccount(accountName)
  }

  private def transferMoney(fromId: Long, toId: Long, amount: BigDecimal) = {
    val fromAccount = accounts(fromId)
    val toAccount = accounts(toId)
    val replyTo = sender
    fromAccount ! BankAccountSupportedOperations.WithdrawMoney(amount)
    awaitingOperationsFromWithPostOpOnSuccess(fromAccount) += new SuccessAwaitingOperation({
      toAccount ! BankAccountSupportedOperations.DepositMoney(amount)
      awaitingOperationsFromWithPostOpOnSuccess(toAccount) += newSuccessAwaitingOperation(fromId, replyTo)
    }, fromId, replyTo)
  }

  private def getAccountDetails(accountId: Long) = {
    val account = accounts(accountId)
    account ! BankAccountSupportedOperations.GetAccountDetails
    awaitingOperationsFromWithPostOpOnSuccess(account) += new SuccessAwaitingOperation({}, accountId, sender)
  }

  private def depositMoney(accountId: Long, amount: BigDecimal) = {
    val account = accounts(accountId)
    account ! BankAccountSupportedOperations.DepositMoney(amount)
    awaitingOperationsFromWithPostOpOnSuccess(account) += newSuccessAwaitingOperation(accountId, sender)
  }

  private def withdrawMoney(accountId: Long, amount: BigDecimal) = {
    val account = accounts(accountId)
    account ! BankAccountSupportedOperations.WithdrawMoney(amount)
    awaitingOperationsFromWithPostOpOnSuccess(account) += newSuccessAwaitingOperation(accountId, sender)
  }

  private def receivedSucceededOperation() = {
    awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue().execute()
  }

  private def receivedFailedOperation() = {
    val awaitingOperation = awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue()
    awaitingOperation.originalSender ! Failure(awaitingOperation.accountId)
  }

  private def receivedBankAccountDetails(accountName: String, accountBalance: BigDecimal) = {
    val awaitingOperation = awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue()
    awaitingOperation.originalSender ! BankSupportedOperations.BankAccountDetails(awaitingOperation.accountId, accountName, accountBalance)
  }

  private def newSuccessAwaitingOperation(accountId: Long, replyTo: ActorRef) =
    new SuccessAwaitingOperation(replyTo ! Success(accountId), accountId, replyTo)

  private def nextAccountId() = {
    val previousAccountId = accountIdCounter
    accountIdCounter += 1
    previousAccountId
  }
}

private[actor] class SuccessAwaitingOperation(op: => Unit, val accountId: Long, val originalSender: ActorRef) {
  def execute(): Unit = op
}
