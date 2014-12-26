package models.tournament

import models.round.{PlayoffRound, GroupRound}
import models.team.Team
import modules.ServiceModule
import org.scalatest.FunSuite
import scaldi.Injector

class TournamentImplTest extends FunSuite {

  implicit val inj = new ServiceModule

  val rules = GameRules(0, 1, 3)

  test("should create preliminary playoff round") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 8)
    val t9 = new Team(9, 9, 9)
    val t10 = new Team(10, 10, 0)

    val t = new TournamentImpl(List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10), "Test tournament")(rules)

    //when
    val tournament = t.doStep()

    //then
    assert(tournament.rounds.size == 1)
    assert(!tournament.isFinished)
    assert(tournament.rounds(0).isInstanceOf[PlayoffRound])
    assert(tournament.rounds(0).teams.size == 4)
    assert(tournament.rounds(0).teams.count(t => t == t10 || t == t1 || t == t2 || t == t3) == 4)
  }

  test("should not create preliminary round - create group round instead") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 8)
    val t9 = new Team(9, 1, 1)
    val t10 = new Team(10, 2, 2)
    val t11 = new Team(11, 3, 3)
    val t12 = new Team(12, 4, 4)
    val t13 = new Team(13, 5, 5)
    val t14 = new Team(14, 6, 6)
    val t15 = new Team(15, 7, 7)
    val t16 = new Team(16, 8, 8)
    val t17 = new Team(17, 1, 1)
    val t18 = new Team(18, 2, 2)
    val t19 = new Team(19, 3, 3)
    val t20 = new Team(20, 4, 4)
    val t21 = new Team(21, 5, 5)
    val t22 = new Team(22, 6, 6)
    val t23 = new Team(23, 7, 7)
    val t24 = new Team(24, 8, 8)
    val t25 = new Team(25, 1, 1)
    val t26 = new Team(26, 2, 2)
    val t27 = new Team(27, 3, 3)
    val t28 = new Team(28, 4, 4)
    val t29 = new Team(29, 5, 5)
    val t30 = new Team(30, 6, 6)
    val t31 = new Team(31, 7, 7)
    val t32 = new Team(32, 8, 8)

    val t = new TournamentImpl(List(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19,
      t20, t21, t22, t23, t24, t25, t26, t27, t28, t29, t30, t31, t32), "Test tournament")(rules)

    //when
    val tournament = t.doStep()

    //then
    assert(tournament.rounds.size == 1)
    assert(tournament.rounds(0).isInstanceOf[GroupRound])
    assert(tournament.rounds(0).teams.size == 32)
  }

  test("should not create preliminary round - create playoff round instead") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 8)

    val t = new TournamentImpl(List(t1, t2, t3, t4, t5, t6, t7, t8), "Test tournament")(rules)

    //when
    val tournament = t.doStep()

    //then
    assert(tournament.rounds.size == 1)
    assert(!tournament.isFinished)
    assert(tournament.rounds(0).isInstanceOf[PlayoffRound])
    assert(tournament.rounds(0).teams.size == 8)
  }

  test("should process tournament - integration test") {
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 8)
    val t9 = new Team(9, 9, 0)

    val t = new TournamentImpl(List(t1, t2, t3, t4, t5, t6, t7, t8, t9), "Test tournament")(rules)

    val tournament = t.doStep()

    assert(tournament.rounds.size == 1)
    assert(!tournament.isFinished)
    assert(tournament.rounds(0).isInstanceOf[PlayoffRound])
    assert(tournament.rounds(0).teams.size == 2)
    assert(!tournament.rounds(0).isFinished)

    val tournament1 = tournament.doStep().doStep().doStep().doStep()

    assert(tournament1.rounds.size == 1)
    assert(!tournament1.isFinished)
    assert(tournament1.rounds(0).isFinished)

    val tournament2 = tournament1.doStep()

    assert(tournament2.teams.size == 9)
    assert(!tournament2.isFinished)
    assert(tournament2.rounds.size == 2)
    assert(!tournament2.rounds(0).isFinished)

    val tournament3 = tournament2.doStep().doStep().doStep().doStep()

    assert(!tournament3.isFinished)
    assert(tournament3.rounds.size == 2)
    assert(tournament3.rounds(0).isFinished)

    val tournament4 = tournament3.doStep()

    assert(tournament4.teams.size == 9)
    assert(!tournament4.isFinished)
    assert(tournament4.rounds.size == 3)
    assert(!tournament4.rounds(0).isFinished)

    val tournament5 = tournament4.doStep().doStep().doStep().doStep()

    assert(!tournament5.isFinished)
    assert(tournament5.rounds.size == 3)
    assert(tournament5.rounds(0).isFinished)

    val tournament6 = tournament5.doStep()

    assert(tournament6.teams.size == 9)
    assert(!tournament6.isFinished)
    assert(tournament6.rounds.size == 4)
    assert(!tournament6.rounds(0).isFinished)
    assert(tournament6.rounds(0).isFinalRound)

    val tournament7 = tournament6.doStep().doStep().doStep().doStep()

    assert(tournament7.teams.size == 9)
    assert(tournament7.isFinished)
    assert(tournament7.rounds.size == 4)
    assert(tournament7.rounds.forall(_.isFinished))
  }
}
