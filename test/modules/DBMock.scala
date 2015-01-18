package modules

import db.dao._match.MatchDao
import db.dao.city.CityDao
import db.dao.round.RoundDao
import db.dao.territory.TerritoryDao
import db.dao.unit.UnitDao
import models.territory.{City, Territory}
import models.tournament.{GameRules, TournamentImpl}
import org.specs2.mock.Mockito
import scaldi.Module

import scala.slick.jdbc.JdbcBackend

//todo: a w sumie to czemu tego nie wstrzykiwać?
object DBMock extends Mockito {

  val ter5 = new Territory(5, "World", 1112129931, None, "W", false, true)
  val ter4 = new Territory(4, "Europe", 112129931, Some(ter5), "EU", false, true)
  val ter3 = new Territory(3, "Poland", 12129931, Some(ter4), "PL", true, true)
  val ter2 = new Territory(2, "Lubelskie", 2129931, Some(ter3), "PLPK", false, true)
  val ter1 = new Territory(1, "Podkarpackie", 2129951, Some(ter3), "PLPK", false, true)

  val city1 = new City(1, "Rzeszów", 182028, 4, ter1, 0, 0)
  val city2 = new City(2, "Przemyśl", 64276, 2, ter1, 0, 0)
  val city3 = new City(3, "Ustrzyki Dolne", 182028, 3, ter1, 0, 0)
  val city4 = new City(4, "Ustrzyki Górne", 64276, 1, ter1, 0, 0)
  val city5 = new City(5, "Warsaw", 264276, 0, ter3, 0, 0)
  val city6 = new City(6, "Budapest", 164276, 0, ter4, 0, 0)
  val city7 = new City(7, "New York", 1164276, 0, ter5, 0, 0)

  val rules = GameRules(0, 1, 3)

  val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
  val tour1 = new TournamentImpl(tr, city1 :: city2 :: Nil, "Test 1 tournament", Nil, Some(1))(rules)(new Module {})

  def prepareCityDaoMockData(cityDao: CityDao): CityDao = {
    cityDao.fromId(org.mockito.Matchers.eq(Long.box(1)))(any[JdbcBackend#Session]) returns Some(city1)
    cityDao.fromId(org.mockito.Matchers.eq(Long.box(2)))(any[JdbcBackend#Session]) returns Some(city2)
    cityDao.fromId(org.mockito.Matchers.eq(Long.box(3)))(any[JdbcBackend#Session]) returns Some(city3)
    cityDao.fromId(org.mockito.Matchers.eq(Long.box(4)))(any[JdbcBackend#Session]) returns Some(city4)
    cityDao.fromId(org.mockito.Matchers.eq(Long.box(99)))(any[JdbcBackend#Session]) returns None
    cityDao.saveOrUpdate(any[City])(any[JdbcBackend#Session]) returns 1
  }

  def prepareTerritoryDaoMockData(territoryDao: TerritoryDao): TerritoryDao = {
    territoryDao.fromId(org.mockito.Matchers.eq(Long.box(1)))(any[JdbcBackend#Session]) returns Some(ter1)
    territoryDao.fromId(org.mockito.Matchers.eq(Long.box(2)))(any[JdbcBackend#Session]) returns Some(ter2)
    territoryDao.fromId(org.mockito.Matchers.eq(Long.box(3)))(any[JdbcBackend#Session]) returns Some(ter3)
    territoryDao.fromId(org.mockito.Matchers.eq(Long.box(99)))(any[JdbcBackend#Session]) returns None

    territoryDao.getChildrenTerritories(org.mockito.Matchers.eq(ter5))(any[JdbcBackend#Session]) returns List(ter4)
    territoryDao.getChildrenTerritories(org.mockito.Matchers.eq(ter4))(any[JdbcBackend#Session]) returns List(ter3)
    territoryDao.getChildrenTerritories(org.mockito.Matchers.eq(ter3))(any[JdbcBackend#Session]) returns List(ter2)
    territoryDao.getChildrenTerritories(org.mockito.Matchers.eq(ter2))(any[JdbcBackend#Session]) returns Nil
  }

  def prepareMatchDaoMockData(matchDao: MatchDao): MatchDao = {
    matchDao.getFixturesWithinUnit(org.mockito.Matchers.eq(Long.box(1)))(any[JdbcBackend#Session]) returns Nil
  }

  def prepareUnitDaoMockData(unitDao: UnitDao): UnitDao = {
    unitDao.getAllWithinRound(org.mockito.Matchers.eq(Long.box(1)))(any[JdbcBackend#Session]) returns Nil
  }

  def prepareRoundDaoMockData(roundDao: RoundDao): RoundDao = {
    roundDao.getTournamentRounds(org.mockito.Matchers.eq(Long.box(1)))(any[JdbcBackend#Session]) returns Nil
  }
}
