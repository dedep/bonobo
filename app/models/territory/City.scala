package models.territory

import models.BaseEntity
import models.team.Team

class City(override val id: Long, override val name: String, val population: Int, val points: Int, container: => Territory,
            val latitude: Double, val longitude: Double) extends Team(id, population, points, name) with Containable with BaseEntity {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}
