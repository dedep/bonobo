package models.territory

import models.BaseEntity

class City(val id: Option[Long], val name: String, val population: Int, val points: Int, container: => Territory,
            val latitude: Double, val longitude: Double) extends Containable with BaseEntity {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}
