package models.actor

object BankSupportedOperations {
  case class OpenAccount(accountName: String)
  case class DepositMoney(amount: BigDecimal)
  case class WithdrawMoney(amount: BigDecimal)
  case object GetDetails

  case object Success
  case object Failure

  case class BankAccountDetails(accountName: String, accountBalance: BigDecimal)
}
