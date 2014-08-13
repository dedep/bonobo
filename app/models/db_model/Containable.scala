package models.db_model

trait Containable {

  /**
   * Given: this -> A -> B -> C
   * @return Territories that contains given Containable, with specified ordering: (A, B, C)
   */
  val getContainers: Seq[Territory]

  protected def appendParentContainers(acc: List[Territory]): List[Territory] = acc.head.container match {
    case None => acc.reverse
    case Some(pt: Territory) => appendParentContainers(pt :: acc)
  }
}
