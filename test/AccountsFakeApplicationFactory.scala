import org.scalatestplus.play.FakeApplicationFactory
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.api.inject.DefaultApplicationLifecycle
import play.core.DefaultWebCommands

trait AccountsFakeApplicationFactory extends FakeApplicationFactory {

  private class AccountsFakeApplicationBuilder {
    def build(): Application = {
      val env = Environment.simple()
      val context = ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration.load(env),
        lifecycle = new DefaultApplicationLifecycle()
      )
      val loader = new AccountsApplicationLoader()
      loader.load(context)
    }
  }

  override def fakeApplication(): Application = new AccountsFakeApplicationBuilder().build()
}
