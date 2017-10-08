package models.actor

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}

import scala.collection.mutable

class Bank(bankAccountMaker: (ActorRefFactory, String) => ActorRef = (f, accountName) => f.actorOf(Props[BankAccount], accountName))
  extends Actor {

  private var accountIdCounter = 0L
  private val accounts = new mutable.LongMap[ActorRef]()

  private val awaitingOperationsFromWithPostOpOnSuccess =
    new mutable.HashMap[ActorRef, mutable.Queue[SuccessAwaitingOperation]]().withDefaultValue(new mutable.Queue())

  import BankSupportedOperations._
  import SupportedOperations._

  override def receive: Receive = {
    case msg@OpenAccount(accountName) =>
      val newAccount = bankAccountMaker(context, accountName)
      val accountId = nextAccountId()
      accounts += accountId -> newAccount
      awaitingOperationsFromWithPostOpOnSuccess(newAccount) += successAwaitingOperation(accountId, sender)
      newAccount ! msg

    case TransferMoney(fromId, toId, amount) =>
      val fromAccount = accounts(fromId)
      val toAccount = accounts(toId)
      val replyTo = sender
      fromAccount ! BankAccountSupportedOperations.WithdrawMoney(amount)
      awaitingOperationsFromWithPostOpOnSuccess(fromAccount) += new SuccessAwaitingOperation({
        toAccount ! BankAccountSupportedOperations.DepositMoney(amount)
        awaitingOperationsFromWithPostOpOnSuccess(toAccount) += successAwaitingOperation(fromId, replyTo)
      }, fromId, replyTo)

    case GetAccountDetails(accountId) =>
      val account = accounts(accountId)
      account ! BankAccountSupportedOperations.GetAccountDetails
      awaitingOperationsFromWithPostOpOnSuccess(account) += new SuccessAwaitingOperation({}, accountId, sender)

    case DepositMoney(accountId, amount) =>
      val account = accounts(accountId)
      account ! BankAccountSupportedOperations.DepositMoney(amount)
      awaitingOperationsFromWithPostOpOnSuccess(account) += successAwaitingOperation(accountId, sender)

    case WithdrawMoney(accountId, amount) =>
      val account = accounts(accountId)
      account ! BankAccountSupportedOperations.WithdrawMoney(amount)
      awaitingOperationsFromWithPostOpOnSuccess(account) += successAwaitingOperation(accountId, sender)

    case BankAccountSupportedOperations.Success => awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue().execute()

    case BankAccountSupportedOperations.Failure =>
      val awaitingOperation = awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue()
      awaitingOperation.originalSender ! Failure(awaitingOperation.accountId)

    case BankAccountSupportedOperations.BankAccountDetails(accountName, accountBalance) =>
      val awaitingOperation = awaitingOperationsFromWithPostOpOnSuccess(sender).dequeue()
      awaitingOperation.originalSender ! BankSupportedOperations.BankAccountDetails(awaitingOperation.accountId, accountName, accountBalance)
  }

  private def successAwaitingOperation(accountId: Long, replyTo: ActorRef) =
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
