package models.exchange

import play.api.libs.json.{Format, Json}

case class AccountInfoResponse(accountName: String, balance: BigDecimal)

object AccountInfoResponse {
  implicit val format: Format[AccountInfoResponse] = Json.format
}
