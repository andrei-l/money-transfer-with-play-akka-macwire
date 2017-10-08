package models.actor

object SupportedOperations {
  case class OpenAccount(accountName: String)

  object SupportedResponses {
    case object Ok
    case class FailedOperation(msg: String)
    case class BankAccountDetails(accountName: String, accountBalance: BigDecimal)
  }
}

object BankSupportedOperations {
  case class DepositMoney(accountId: Long, amount: BigDecimal)
  case class WithdrawMoney(accountId: Long, amount: BigDecimal)
  case class GetAccountDetails(accountId: Long)
  case class TransferMoney(accountIdFrom: Long, accountIdTo: Long, amount: BigDecimal)

  object SupportedResponses {
    case class AccountCreated(accountId: Long)
  }
}

private[actor] object BankAccountSupportedOperations {
  case object GetAccountDetails
  case class DepositMoney(amount: BigDecimal)
  case class WithdrawMoney(amount: BigDecimal)
}

