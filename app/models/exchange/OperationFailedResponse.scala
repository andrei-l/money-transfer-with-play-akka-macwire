package models.exchange

import play.api.libs.json.{Format, Json}

case class OperationFailedResponse(msg: String)

object OperationFailedResponse {
  implicit val format: Format[OperationFailedResponse] = Json.format
}





