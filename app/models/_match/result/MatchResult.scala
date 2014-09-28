package models._match.result

abstract class MatchResult {
  val aGoals: Int
  val bGoals: Int

  require(aGoals >= 0 && bGoals >= 0)

  override def toString: String = aGoals + " : " + bGoals
}