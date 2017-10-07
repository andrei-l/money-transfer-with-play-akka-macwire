package models.actor

import akka.actor.Actor

private[actor] class BankAccount extends Actor {
  private var name: Option[String] = None
  private var balance: BigDecimal = 0

  import SupportedOperations._
  import BankAccountSupportedOperations._

  override def receive: Receive = {
    case OpenAccount(accountName) => successfulOperation {
      this.name = Some(accountName)
      context.become(openedAccount)
    }
  }

  private def openedAccount: Receive = {
    case DepositMoney(amount) => successfulOperation(balance += amount)
    case WithdrawMoney(amount) =>
      if (balance >= amount) successfulOperation(balance -= amount)
      else sender ! Failure
    case GetAccountDetails => sender ! BankAccountDetails(name.get, balance)
  }

  private def successfulOperation(op: => Unit): Unit = {
    op
    sender ! Success
  }
}