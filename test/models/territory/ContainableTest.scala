package models.territory

import org.specs2.mutable.Specification

class ContainableTest extends Specification {
  "should return parent containers for city" in {
    //given
    val ctr1 = new Territory("CCD", "World", 100000000, None, false, true)
    val ctr2 = new Territory("CCD", "Europe", 1000000, Some(ctr1), false, true)
    val ctr3 = new Territory("CCD", "Poland", 100000, Some(ctr2), false, true)
    val city1 = new City(0, "Lublin", 300000, 500000, ctr3, 0, 0)

    //when
    val city1Containers = city1.getContainers

    //then
    city1Containers should have size 3
    city1Containers should containAllOf(ctr1 :: ctr2 :: ctr3 :: Nil)
  }

  "should return parent containers for territory" in {
    //given
    val ctr1 = new Territory("W", "World", 100000000, None, false, true)
    val ctr2 = new Territory("EU", "Europe", 1000000, Some(ctr1), false, true)

    //when
    val ctr2Containers = ctr2.getContainers

    //then
    ctr2Containers should have size 1
    ctr2Containers should contain(ctr1)
  }

  "should return parent containers for super territory" in {
    //given
    val ctr1 = new Territory("W", "World", 100000000, None, false, true)

    //when
    val ctr1Containers = ctr1.getContainers

    //then
    ctr1Containers should beEmpty
  }
}
