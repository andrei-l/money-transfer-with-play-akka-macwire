import akka.actor.ActorRef
import controllers.AccountsController
import play.api.mvc.ControllerComponents

trait MoneyTransferModule {

  import com.softwaremill.macwire._

  def controllerComponents: ControllerComponents
  def bank: ActorRef

  lazy val accountsController: AccountsController = wire[AccountsController]

}
