package models.dedep.bonobo.core._match.result

case class WinA(override val aGoals: Int, override val bGoals: Int) extends MatchResult {
  require(aGoals > bGoals)
}