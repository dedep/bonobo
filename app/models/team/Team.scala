package models.team

//todo: do wywalenia - zastąpić przez City
class Team(val id: Option[Long], val value: Int, val rankPoints: Int, val name: String = "") {
  override def toString: String = name

  def canEqual(other: Any): Boolean = other.isInstanceOf[Team]

  override def equals(other: Any): Boolean = other match {
    case that: Team =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}