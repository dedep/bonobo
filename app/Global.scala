import modules.{DaoModule, ControllerModule}
import play.api.GlobalSettings
import scaldi.Injector
import scaldi.play.ScaldiSupport

object Global extends GlobalSettings with ScaldiSupport {
  override def applicationModule: Injector = new ControllerModule :: new DaoModule
}