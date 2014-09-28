package controllers

import play.api.mvc._
import scaldi.{Injector, Injectable}

class AppController(implicit inj: Injector) extends Controller with Injectable {
  private val territoryController = inject[TerritoryController]

  def world = territoryController.findByCode("W")
}
