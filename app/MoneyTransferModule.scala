import controllers.AccountsController
import play.api.mvc.ControllerComponents

trait MoneyTransferModule {

  import com.softwaremill.macwire._

  def controllerComponents: ControllerComponents

  lazy val accountsController: AccountsController = wire[AccountsController]

}
