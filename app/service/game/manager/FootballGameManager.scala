package service.game.manager

class FootballGameManager extends GameManager {
  override val POINTS_FOR_LOSE: Double = 0
  override val POINTS_FOR_DRAW: Double = 1
  override val POINTS_FOR_WIN: Double = 3
}
