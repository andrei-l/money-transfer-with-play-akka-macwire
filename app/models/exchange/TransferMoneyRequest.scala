package models.exchange

import play.api.libs.json.{Format, Json}

case class TransferMoneyRequest(destinationAccountId: Long, amount: BigDecimal)

object TransferMoneyRequest {
  implicit val format: Format[TransferMoneyRequest] = Json.format
}
