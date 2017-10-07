package models.exchange

import play.api.libs.json.{Format, Json}

case class OpenAccountRequest(accountName: String)

object OpenAccountRequest {
  implicit val format: Format[OpenAccountRequest] = Json.format
}

