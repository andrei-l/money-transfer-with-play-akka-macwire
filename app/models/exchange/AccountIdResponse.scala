package models.exchange

import play.api.libs.json.{Format, Json}

case class AccountIdResponse(id: Long)

object AccountIdResponse {
  implicit val format: Format[AccountIdResponse] = Json.format
}




