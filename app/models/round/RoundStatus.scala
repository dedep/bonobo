package models.round

object RoundStatus extends Enumeration {
  val DRAW_POTS, DRAW_UNITS, PLAY_FIXTURE, FINISHED = Value

  type RoundStatus = Value
}