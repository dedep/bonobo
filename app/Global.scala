import filters.CorsHeadersFilter
import modules._
import play.api.GlobalSettings
import play.api.mvc.WithFilters
import scaldi.Injector
import scaldi.play.ScaldiSupport

object Global extends WithFilters(CorsHeadersFilter) with GlobalSettings with ScaldiSupport {
  override def applicationModule: Injector =
    new ControllerModule :: new DaoModule :: new ServiceModule :: new ValidatorModule :: new RowMapperModule
}
