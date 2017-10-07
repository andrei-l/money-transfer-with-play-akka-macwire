package models.exchange

import play.api.libs.json.{Format, Json}

case class DepositMoneyRequest(amount: BigDecimal)

object DepositMoneyRequest {
  implicit val format: Format[DepositMoneyRequest] = Json.format
}

