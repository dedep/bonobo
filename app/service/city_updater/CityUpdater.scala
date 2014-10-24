package service.city_updater

import models.territory.City

import scala.slick.jdbc.JdbcBackend

trait CityUpdater {
  def update(city: City)(implicit rs: JdbcBackend#Session): Boolean
}
