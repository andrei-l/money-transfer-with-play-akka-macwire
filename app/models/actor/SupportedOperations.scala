package models.actor

object SupportedOperations {
  case class OpenAccount(accountName: String)
  case object GetAccountDetails

  case object Success
  case object Failure

  case class BankAccountDetails(accountName: String, accountBalance: BigDecimal)
}

object BankSupportedOperations {
  case class TransferMoney(accountIdFrom: Long, accountIdTo: Long, amount: BigDecimal)
}

private[actor] object BankAccountSupportedOperations {
  case class DepositMoney(amount: BigDecimal)
  case class WithdrawMoney(amount: BigDecimal)
}
