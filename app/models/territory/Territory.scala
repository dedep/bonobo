package models.territory

import play.api.libs.json._

//todo: jakiś mechanizm trzeba zdefiniować żeby nie zapętlić hierarchii terytoriów
class Territory(val id: Long, val name: String, val population: Long, parent: => Option[Territory], val code: String)
  extends Containable {

  lazy val container = parent

  override lazy val getContainers: List[Territory] = container match {
    case None => Nil
    case Some(territory: Territory) => appendParentContainers(territory :: Nil)
  }
}

object Territory {
  implicit object TerritoryFormat extends Format[Territory] {

    def writes(t: Territory): JsValue = {
      val citySeq = Seq(
        "id" -> JsNumber(t.id),
        "name" -> JsString(t.name),
        "population" -> JsNumber(t.population),
        "container" -> t.container.map(writes).getOrElse(JsNull)
      )
      JsObject(citySeq)
    }

    def reads(json: JsValue): JsResult[Territory] = {
      JsSuccess(new Territory(1, "", 2, None, ""))
    }
  }
}

