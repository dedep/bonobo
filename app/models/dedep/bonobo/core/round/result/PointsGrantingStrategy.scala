package models.dedep.bonobo.core.round.result

trait PointsGrantingStrategy {
  val POINTS_FOR_WIN: Double
  val POINTS_FOR_DRAW: Double
  val POINTS_FOR_LOSE: Double
}

object FootballPointsGrantingStrategy extends PointsGrantingStrategy {
  override val POINTS_FOR_LOSE: Double = 0
  override val POINTS_FOR_DRAW: Double = 1
  override val POINTS_FOR_WIN: Double = 3
}
