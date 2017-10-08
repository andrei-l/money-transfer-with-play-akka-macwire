package models.actor

object SupportedOperations {
  case class OpenAccount(accountName: String)
}

object BankSupportedOperations {
  case class DepositMoney(accountId: Long, amount: BigDecimal)
  case class WithdrawMoney(accountId: Long, amount: BigDecimal)
  case class GetAccountDetails(accountId: Long)
  case class TransferMoney(accountIdFrom: Long, accountIdTo: Long, amount: BigDecimal)
  case class Success(accountId: Long)
  case class Failure(accountId: Long)

  case class BankAccountDetails(accountId: Long, accountName: String, accountBalance: BigDecimal)
}

private[actor] object BankAccountSupportedOperations {
  case object GetAccountDetails
  case class DepositMoney(amount: BigDecimal)
  case class WithdrawMoney(amount: BigDecimal)

  case object Success
  case object Failure

  case class BankAccountDetails(accountName: String, accountBalance: BigDecimal)
}
