package models.dedep.bonobo.core.team

class Team(val value: Int, val rankPoints: Int, val name: String = "") {
  override def toString: String = rankPoints.toString
}