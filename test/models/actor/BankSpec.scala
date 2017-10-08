package models.actor

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class BankSpec extends TestKit(ActorSystem("BankSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  private val accountProb = TestProbe()
  private val bankAccountMaker: (ActorRefFactory, String) => ActorRef = (_, _) => accountProb.ref
  private val bankActor = system.actorOf(Props(classOf[Bank], bankAccountMaker))
  private var accountId1: Long = _
  private var accountId2: Long = _

  import models.actor.BankSupportedOperations._
  import models.actor.SupportedOperations._

  "A Bank Actor" should {
    "open multiple accounts" in {
      accountId1 = openAccount("acc")
      accountId2 = openAccount("acc")
      assert(accountId1 !== accountId2)
    }

    "deposit money to accounts" in {
      depositMoney(accountId1, 100)
      depositMoney(accountId2, 70)
    }

    "transfer money between accounts" in {
      transferMoney(accountId1, accountId2, 20)
    }

    "withdraw money from account" in {
      withdrawMoney(accountId2, 20)
    }

    "get account details" in {
      getAccountDetails(accountId2)
    }
  }

  private def openAccount(accountName: String): Long = {
    bankActor ! OpenAccount(accountName)
    accountProb.expectMsg(OpenAccount(accountName))
    accountProb.reply(BankAccountSupportedOperations.Success)

    expectMsgClass(classOf[BankSupportedOperations.Success]).accountId
  }

  private def depositMoney(accountId: Long, amount: BigDecimal) = {
    bankActor ! DepositMoney(accountId, amount)
    accountProb.expectMsg(BankAccountSupportedOperations.DepositMoney(amount))
    accountProb.reply(BankAccountSupportedOperations.Success)

    expectMsg(Success(accountId))
  }

  private def transferMoney(fromId: Long, toId: Long, amount: BigDecimal) = {
    bankActor ! TransferMoney(fromId, toId, amount)
    accountProb.expectMsg(BankAccountSupportedOperations.WithdrawMoney(amount))
    accountProb.reply(BankAccountSupportedOperations.Success)
    accountProb.expectMsg(BankAccountSupportedOperations.DepositMoney(amount))
    accountProb.reply(BankAccountSupportedOperations.Success)

    expectMsg(Success(fromId))
  }

  private def withdrawMoney(accountId: Long, amount: BigDecimal) = {
    bankActor ! WithdrawMoney(accountId, amount)
    accountProb.expectMsg(BankAccountSupportedOperations.WithdrawMoney(amount))
    accountProb.reply(BankAccountSupportedOperations.Success)

    expectMsg(Success(accountId))
  }

  private def getAccountDetails(accountId: Long) = {
    bankActor ! GetAccountDetails(accountId)
    accountProb.expectMsg(BankAccountSupportedOperations.GetAccountDetails)
    accountProb.reply(BankAccountSupportedOperations.BankAccountDetails("bank-acc", 30))

    expectMsg(BankAccountDetails(accountId, "bank-acc", 30))
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}
