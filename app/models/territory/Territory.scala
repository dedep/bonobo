package models.territory

//todo: jakiś mechanizm trzeba zdefiniować żeby nie zapętlić hierarchii terytoriów
class Territory(val code: String, val name: String, val population: Long, parent: => Option[Territory],
                val isCountry: Boolean, val modifiable: Boolean) extends Containable {

  lazy val container = parent

  override lazy val getContainers: List[Territory] =
    container.map(t => appendParentContainers(t :: Nil)).getOrElse(Nil)
}

