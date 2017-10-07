import models.exchange._
import org.eclipse.jetty.http.HttpStatus.Code
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.{BaseOneServerPerSuite, PlaySpec}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Results
import play.api.test.WsTestClient
import play.mvc.Http.Status

class AccountsApplicationIntegrationTest extends PlaySpec
  with BaseOneServerPerSuite
  with AccountsFakeApplicationFactory
  with Results
  with ScalaFutures
  with IntegrationPatience {

  "Accounts API" should {
    "create account" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl("/account").post(toJson(OpenAccountRequest("new-acc")))) {
          response => {
            response.body[JsValue].as[AccountIdResponse] mustBe AccountIdResponse(1)
            response.status mustBe Code.CREATED.getCode
          }
        }
      }
    }

    "deposit money" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl("/account/1/deposit").post(toJson(DepositMoneyRequest(200)))) {
          response => {
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }

    "transfer money" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl("/account/1/transfer-money").post(toJson(TransferMoneyRequest(3, 30)))) {
          response => {
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }

    "load account" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl("/account/1").get()) {
          response => {
            response.body[JsValue].as[AccountInfoResponse] mustBe AccountInfoResponse("", 150)
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }
  }
}
