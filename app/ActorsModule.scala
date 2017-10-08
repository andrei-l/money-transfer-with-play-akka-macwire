import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import models.actor.{Bank, BankAccount}

trait ActorsModule {
  def actorSystem: ActorSystem

  lazy val bank: ActorRef = actorSystem.actorOf(
    Props(classOf[Bank], (f: ActorRefFactory, accountName: String) => f.actorOf(Props[BankAccount], accountName))
  )
}
