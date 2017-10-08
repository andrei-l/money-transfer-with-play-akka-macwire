import models.exchange._
import org.eclipse.jetty.http.HttpStatus.Code
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.{BaseOneServerPerSuite, PlaySpec}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.test.WsTestClient

class AccountsApplicationIntegrationTest extends PlaySpec
  with BaseOneServerPerSuite
  with AccountsFakeApplicationFactory
  with Results
  with ScalaFutures
  with IntegrationPatience {

  private var accountId1: Long = _
  private var accountId2: Long = _

  "Accounts API" should {
    "create account" in {
      WsTestClient.withClient { implicit client =>
        accountId1 = createAccount("new-acc")
        accountId2 = createAccount("acc32")
      }
    }

    "deposit money" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl(s"/account/$accountId1/deposit").post(toJson(DepositMoneyRequest(200)))) {
          response => {
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }

    "transfer money" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl(s"/account/$accountId1/transfer-money").post(toJson(TransferMoneyRequest(accountId2, 30)))) {
          response => {
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }

    "withdraw money" in {
      WsTestClient.withClient { implicit client =>
        whenReady(wsUrl(s"/account/$accountId2/withdraw").post(toJson(WithdrawMoneyRequest(5)))) {
          response => {
            response.status mustBe Code.OK.getCode
          }
        }
      }
    }

    "load account" in {
      WsTestClient.withClient { implicit client =>
        loadAccount(accountId1, AccountInfoResponse("new-acc", 170))
        loadAccount(accountId2, AccountInfoResponse("acc32", 25))
      }
    }
  }

  private def createAccount(accountName: String)(implicit client: WSClient): Long = {
    whenReady(wsUrl("/account").post(toJson(OpenAccountRequest(accountName)))) {
      response => {
        val accountIdResponse = response.body[JsValue].as[AccountIdResponse]
        response.status mustBe Code.CREATED.getCode
        accountIdResponse.id
      }
    }
  }

  private def loadAccount(accountId: Long, expectedAccountInfo: AccountInfoResponse)(implicit client: WSClient): Unit = {
    whenReady(wsUrl(s"/account/$accountId").get()) {
      response => {
        response.body[JsValue].as[AccountInfoResponse] mustBe expectedAccountInfo
        response.status mustBe Code.OK.getCode
      }
    }
  }
}
