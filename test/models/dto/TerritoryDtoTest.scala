package models.dto

import models.territory.Territory
import org.specs2.mutable.Specification
import play.api.libs.json.{JsObject, JsUndefined, JsString, JsNumber}

class TerritoryDtoTest extends Specification {

  "Should convert territory to json without container" in {
    //given
    val t = new Territory(1, "test-tr", 500100900, None, "TTR")

    //when
    val result = TerritoryDto.parse(t).toJson

    //then
    result \ "id" mustEqual JsNumber(1)
    result \ "name" mustEqual JsString("test-tr")
    result \ "population" mustEqual JsNumber(500100900)
    result \ "code" mustEqual JsString("TTR")
    result \ "container" must beAnInstanceOf[JsUndefined]
  }

  "Should convert territory to json with container" in {
    //given
    val container = new Territory(2, "p-terr", 100, None, "PTR")
    val t = new Territory(1, "test-tr", 500100900, Some(container), "TTR")

    //when
    val result = TerritoryDto.parse(t).toJson

    //then
    result \ "id" mustEqual JsNumber(1)
    result \ "name" mustEqual JsString("test-tr")
    result \ "population" mustEqual JsNumber(500100900)
    result \ "code" mustEqual JsString("TTR")
    result \ "container" must beAnInstanceOf[JsObject]
    result \ "container" \ "name" mustEqual JsString("p-terr")
    result \ "container" \ "code" mustEqual JsString("PTR")
  }
}
