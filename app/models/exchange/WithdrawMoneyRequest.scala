package models.exchange

import play.api.libs.json.{Format, Json}

case class WithdrawMoneyRequest(amount: BigDecimal)

object WithdrawMoneyRequest {
  implicit val format: Format[WithdrawMoneyRequest] = Json.format
}

