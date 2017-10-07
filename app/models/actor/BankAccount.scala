package models.actor

import akka.actor.Actor

object BankAccount {
  case class OpenAccount(accountName: String)
  case class DepositMoney(amount: BigDecimal)
  case class WithdrawMoney(amount: BigDecimal)
  case object GetDetails

  case object Success
  case object Failure

  case class BankAccountDetails(accountName: String, accountBalance: BigDecimal)
}

private[actor] class BankAccount extends Actor {
  private var name: Option[String] = None
  private var balance: BigDecimal = 0

  import BankAccount._

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
    case GetDetails => sender ! BankAccountDetails(name.get, balance)
  }

  private def successfulOperation(op: => Unit): Unit = {
    op
    sender ! Success
  }
}
