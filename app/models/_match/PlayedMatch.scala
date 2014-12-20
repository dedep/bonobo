package models._match

import models._match.result.MatchResult
import models.team.Team
import org.joda.time.DateTime
import scaldi.Injector

case class PlayedMatch(override val aTeam: Team, override val bTeam: Team, result: MatchResult, override val playDate: Option[DateTime],
                       override val id: Option[Long] = None)(implicit inj: Injector)
  extends Match(aTeam, bTeam, id){

  override def toString: String = super.toString + " " + result.toString

  override def eval(): MatchResult =
    throw new IllegalStateException("Cannot eval played match " + this)
}