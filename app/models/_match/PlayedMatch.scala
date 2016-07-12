package models._match

import models._match.result.MatchResult
import models.reverse.RoundUnitInfo
import models.territory.City
import org.joda.time.DateTime
import scaldi.Injector

case class PlayedMatch(override val aTeam: City, override val bTeam: City, result: MatchResult,
                       override val playDate: Option[DateTime], override val id: Option[Long] = None)
                      (override val unitInfo: RoundUnitInfo)
                      (implicit inj: Injector)
  extends Match(aTeam, bTeam, id)(unitInfo){

  override def toString: String = super.toString + " " + result.toString

  override def eval(): MatchResult =
    throw new IllegalStateException("Cannot eval played match " + this)
}