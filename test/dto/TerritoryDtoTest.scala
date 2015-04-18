package dto

import dto.model.TerritoryDto
import models.territory.Territory
import org.specs2.mutable.Specification
import play.api.libs.json._

class TerritoryDtoTest extends Specification {

  "Should convert territory to json without container" in {
    //given
    val t = new Territory("TTR", "test-tr", 500100900, None, false, true)

    //when
    val result = TerritoryDto.parse(t).toJson

    //then
    result \ "name" mustEqual JsString("test-tr")
    result \ "population" mustEqual JsNumber(500100900)
    result \ "code" mustEqual JsString("TTR")
    result \ "parent" must beAnInstanceOf[JsUndefined]
    result \ "isCountry" mustEqual JsBoolean(value = false)
    result \ "modifiable" mustEqual JsBoolean(value = true)
  }

  "Should convert territory to json with container" in {
    //given
    val container = new Territory("PTR", "p-terr", 100, None, false, false)
    val t = new Territory("TTR", "test-tr", 500100900, Some(container), true, false)

    //when
    val result = TerritoryDto.parse(t).toJson

    //then
    result \ "name" mustEqual JsString("test-tr")
    result \ "population" mustEqual JsNumber(500100900)
    result \ "code" mustEqual JsString("TTR")
    result \ "isCountry" mustEqual JsBoolean(value = true)
    result \ "modifiable" mustEqual JsBoolean(value = false)
    result \ "parent" must beAnInstanceOf[JsObject]
    result \ "parent" \ "name" mustEqual JsString("p-terr")
    result \ "parent" \ "code" mustEqual JsString("PTR")
  }
}
