package models.actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class BankAccountSpec extends TestKit(ActorSystem("BankAccountSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {


  var bankAccountActor: Option[ActorRef] = None

  import BankSupportedOperations._

  "A Bank Account Actor" should {
    "open account" in {
      val bankAccountActor = system.actorOf(Props[BankAccount])
      bankAccountActor ! OpenAccount("acc")
      expectMsg(Success)
      this.bankAccountActor = Some(bankAccountActor)
    }

    "deposit money to account" in {
      withBankAccount { account =>
        account ! DepositMoney(200)
        expectMsg(Success)
      }
    }

    "withdraw money from account" in {
      withBankAccount { account =>
        account ! WithdrawMoney(50)
        expectMsg(Success)
      }
    }

    "load account details" in {
      withBankAccount { account =>
        account ! GetDetails
        expectMsg(BankAccountDetails("acc", 150))
      }
    }

    "fail to withdraw more money from account than it has" in {
      withBankAccount { account =>
        account ! WithdrawMoney(200)
        expectMsg(Failure)

        account ! GetDetails
        expectMsg(BankAccountDetails("acc", 150))
      }
    }
  }

  private def withBankAccount(op: ActorRef => AnyRef): Unit = {
    bankAccountActor.map { account =>
      op(account)
    } getOrElse (throw new IllegalArgumentException("Account must exist"))
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
