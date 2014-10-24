package service.city_updater

import akka.util.Timeout
import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.Play.current
import play.api.libs.ws.WS
import scaldi.{Injectable, Injector}

import scala.concurrent._
import scala.concurrent.duration._
import scala.slick.jdbc.JdbcBackend
import scala.xml.Elem

class CityUpdaterImpl(implicit inj: Injector) extends CityUpdater with Injectable {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val cityDao = inject[CityDao]

  private val login = "dedep"

  override def update(city: City)(implicit rs: JdbcBackend#Session): Boolean = {

    implicit val timeout = Timeout(50000 milliseconds)
    val url = prepareUrl(city.name, login)
    val response = WS.url(url).get()

    log.info("Fetching city [" + city.name + "] data from GeoNames Web Service. Url = " + url)

    try {
      val resultXml = Await.result(response, timeout.duration).xml

      val updatedCity = new City(city.id, city.name, extractPopulation(resultXml).get, city.points, city.territory,
        extractLatitude(resultXml).get, extractLongitude(resultXml).get)

      log.info("Got successful response for city [" + city.name + "] from GeoNames Web Service")

      cityDao.saveOrUpdate(updatedCity)

      true
    } catch {
      case e: Exception =>
        log.error("Cannot receive response from GeoNames for city: " + city.name + ". Exception " + e)
        false
    }
  }

  private def prepareUrl(cityName: String, login: String) =
    ("http://api.geonames.org/search?name_equals=" + cityName + "&maxRows=1&style=LONG&cities=cities1000&username=" + login)
      .replace(" ", "%20")

  private def extractPopulation(resultXml: Elem) = (resultXml \\ "population").headOption.map(_.text.toInt)

  private def extractLatitude(resultXml: Elem) = (resultXml \\ "lat").headOption.map(_.text.toDouble)

  private def extractLongitude(resultXml: Elem) = (resultXml \\ "lng").headOption.map(_.text.toDouble)
}
