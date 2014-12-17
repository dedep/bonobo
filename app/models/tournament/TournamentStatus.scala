package models.tournament

object TournamentStatus extends Enumeration {
  val NOT_STARTED, PLAYING, FINISHED = Value

  type TournamentStatus = Value
}