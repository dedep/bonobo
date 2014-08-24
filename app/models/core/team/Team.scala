package models.core.team

class Team(val id: Long, val value: Int, val rankPoints: Int, val name: String = "") {
  override def toString: String = name
}