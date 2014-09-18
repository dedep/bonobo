package models.db_model

import org.scalatest.FunSuite

class ContainableTest extends FunSuite {
  test("should return parent containers for city") {
    //given
    val ctr1 = new Territory(0, "World", 100000000, None,"")
    val ctr2 = new Territory(1, "Europe", 1000000, Some(ctr1), "")
    val ctr3 = new Territory(2, "Poland", 100000, Some(ctr2), "")
    val city1 = new City(0, "Lublin", 300000, 500000, ctr3, 0, 0)

    //when
    val city1Containers = city1.getContainers

    //then
    assert(city1Containers.size === 3)
    assert(city1Containers.contains(ctr1))
    assert(city1Containers.contains(ctr2))
    assert(city1Containers.contains(ctr3))
  }

  test("should return parent containers for territory") {
    //given
    val ctr1 = new Territory(0, "World", 100000000, None, "")
    val ctr2 = new Territory(1, "Europe", 1000000, Some(ctr1), "")

    //when
    val ctr2Containers = ctr2.getContainers

    //then
    assert(ctr2Containers.size === 1)
    assert(ctr2Containers.contains(ctr1))
  }

  test("should return parent containers for super territory") {
    //given
    val ctr1 = new Territory(0, "World", 100000000, None, "")

    //when
    val ctr1Containers = ctr1.getContainers

    //then
    assert(ctr1Containers === Nil)
  }
}
