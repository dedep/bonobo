package models.territory

import models.team.Team

class City(override val id: Long, override val name: String, val population: Int, val points: Int, container: => Territory,
            val latitude: Double, val longitude: Double) extends Team(id, population, points, name) with Containable {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}

//object City {
//  implicit object CityFormat extends Format[City] {
//
//    def writes(city: City): JsValue = {
//      val citySeq = Seq(
//        "id" -> JsNumber(city.id),
//        "name" -> JsString(city.name),
//        "points" -> JsNumber(city.points),
//        "population" -> JsNumber(city.population),
//        "territory" -> Territory.TerritoryFormat.writes(city.territory)
//      )
//      JsObject(citySeq)
//    }
//
//    def reads(json: JsValue): JsResult[City] = {
//      JsSuccess(new City(1, "", 1, 1, new Territory(1, "", 2, None, ""), 0d, 0d))
//    }
//  }
//}

