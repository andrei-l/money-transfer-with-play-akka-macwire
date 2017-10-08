import com.softwaremill.macwire._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}
import play.filters.HttpFiltersComponents
import router.Routes

class AccountsApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = new AccountsComponents(context).application
}

class AccountsComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with MoneyTransferModule
  with ActorsModule
  with HttpFiltersComponents {

  override def router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }
}
