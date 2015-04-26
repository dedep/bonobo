package models.territory

import models.BaseEntity

class Territory(override val id: Option[Long], val code: String, val name: String, val population: Long, parent: => Option[Territory],
                val isCountry: Boolean, val modifiable: Boolean) extends Containable with BaseEntity {

  lazy val container = parent

  override lazy val getContainers: List[Territory] =
    container.map(t => appendParentContainers(t :: Nil)).getOrElse(Nil)

  val isWorld = code == "W"

  def canEqual(other: Any): Boolean = other.isInstanceOf[Territory]

  override def equals(other: Any): Boolean = other match {
    case that: Territory =>
      (that canEqual this) &&
        code == that.code
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(code)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

