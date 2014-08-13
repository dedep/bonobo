package models.core._match.result

case class WinB(override val aGoals: Int, override val bGoals: Int) extends MatchResult {
  require(bGoals > aGoals)
}